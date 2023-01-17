package com.srt.message.service.rabbit;

import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.message.SMSMessageDto;
import com.srt.message.repository.BrokerRepository;
import com.srt.message.repository.MessageResultRepository;
import com.srt.message.repository.MessageRuleRepository;
import com.srt.message.repository.redis.MessageResultRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.*;
import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.LG_ROUTING_KEY;

@Log4j2
@Service
@RequiredArgsConstructor
public class BrokerService {
    private final int BROKER_SIZE = 3;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RabbitTemplate rabbitTemplate;

    private final MessageResultRepository messageResultRepository;
    private final MessageResultRedisRepository messageResultRedisRepository;

    private final MessageRuleRepository messageRuleRepository;

    private final BrokerRepository brokerRepository;

    // Broker 서버에게 메시지 전송
    public void sendSmsMessage(BrokerMessageDto brokerMessageDto) {
        SMSMessageDto smsMessageDto = brokerMessageDto.getSmsMessageDto();
        Member member = brokerMessageDto.getMember();


        // KT, LG, SKT 순으로 정렬해서 가져옴
        List<MessageRule> messageRules = messageRuleRepository.findAllByMember(member);

        int count = brokerMessageDto.getCount();

        double value = (count / 100);
        int[] broker_count = new int[BROKER_SIZE]; // ([0] KT, [1] LG, [2] SKT)
        int sum = 0;

        for(int i = 0; i < BROKER_SIZE; i++){
            broker_count[i] = !messageRules.isEmpty() ? (int)(messageRules.get(i).getBrokerRate() * value) : (int)(33 * value); // 중계사 비율 설정 안했을 경우 33%씩 분배
            sum += broker_count[i];
        }

        if (sum < count) // 비율이 안떨어질 경우, 랜덤 중계사에게 배분
            broker_count[(int)(Math.random() * 3)] += count - sum;

        // kt
        sendMsgToBroker(brokerMessageDto, KT_ROUTING_KEY, broker_count[0]);

        // lg
        sendMsgToBroker(brokerMessageDto, LG_ROUTING_KEY, broker_count[1]);

        // skt
        sendMsgToBroker(brokerMessageDto, SKT_ROUTING_KEY, broker_count[2]);
    }

    private void sendMsgToBroker(BrokerMessageDto brokerMessageDto, String routingKey, int broker_count){
        SMSMessageDto smsMessageDto = brokerMessageDto.getSmsMessageDto();
        Message message = brokerMessageDto.getMessage();
        List<Contact> contacts = brokerMessageDto.getContacts();

        String brokerName = routingKey.split("\\.")[1].toUpperCase();
        Broker broker = brokerRepository.findByName(brokerName);

        Set<RMessageResult> rMessageResultSet = new LinkedHashSet<>();


        for (int i = 0; i < broker_count; i++) {
            smsMessageDto.setTo(contacts.get(0).getPhoneNumber()); // TODO 테스트를 위해 0으로 설정, 추후에 i로 변경해야 함
            Contact contact = contacts.get(0); // TODO 테스트를 위해 0으로 설정, 추후에 i로 변경해야 함

            RMessageResult rMessageResult = RMessageResult.builder()
                    .messageId(message.getId())
                    .brokerId(broker.getId())
                    .contactId(null) // contact로 변경 해야함 (테스트 용)
                    .messageStatus(MessageStatus.PENDING)
                    .build();

            MessageResult messageResult = MessageResult.builder()
                    .message(message)
                    .contact(null) // contact로 변경 해야함 (테스트 용)
                    .broker(broker)
                    .messageStatus(MessageStatus.PENDING)
                    .build();

            // TODO RDBMS 저장하는걸 MQ 사용해서 비동기로 처리해야 하나 고민해보기
//            messageResultRepository.save(messageResult);

            // RedisTemplate 사용 (속도 더 빠름)
//            ValueOperations<String, Object> stringStringValueOperations = redisTemplate.opsForValue();
//            stringStringValueOperations.set("test", rMessageResult);

            rMessageResultSet.add(rMessageResult);

//            messageResultRedisRepository.save(rMessageResult);

            rabbitTemplate.convertAndSend(SMS_EXCHANGE_NAME, routingKey, smsMessageDto);
            log.info((i + 1) + " 번째 메시지가 전송되었습니다 - " + routingKey);
        }

        // redis 저장
//        messageResultRedisRepository.saveAll(rMessageResultList);

        // RedisTemplate Set 저장 (속도 더 빠름)
        SetOperations<String, Object> setOperation = redisTemplate.opsForSet();
        setOperation.add("test123", rMessageResultSet);
        System.out.println("종료");
    }
}
