package com.srt.message.service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.service.dto.message.kakao.BrokerKakaoMessageDto;
import com.srt.message.service.dto.message.sms.BrokerMessageDto;
import com.srt.message.service.dto.message.sms.BrokerSendMessageDto;
import com.srt.message.service.dto.message.sms.SMSMessageDto;
import com.srt.message.service.dto.message_result.MessageResultDto;
import com.srt.message.repository.BrokerRepository;
import com.srt.message.repository.MessageResultRepository;
import com.srt.message.repository.MessageRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class BrokerService {
    private final int BROKER_SIZE = 3;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RabbitTemplate rabbitTemplate;

    private final MessageResultRepository messageResultRepository;

    private final MessageRuleRepository messageRuleRepository;

    private final BrokerRepository brokerRepository;

    private final ObjectMapper objectMapper;

    // Broker 서버에게 메시지 전송
    public String sendSmsMessage(BrokerMessageDto brokerMessageDto) {
        // 시간 측정
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

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
        sendMsgToBroker(brokerMessageDto, KT_WORK_ROUTING_KEY, broker_count[0]);

        // lg
        sendMsgToBroker(brokerMessageDto, LG_WORK_ROUTING_KEY, broker_count[1]);

        // skt
        sendMsgToBroker(brokerMessageDto, SKT_WORK_ROUTING_KEY, broker_count[2]);

        // 시간 측정 결과
        stopWatch.stop();
        String processTime = String.valueOf(stopWatch.getTime());
        log.info("Process Time: {} ", processTime);
        return processTime;
    }

    private void sendMsgToBroker(BrokerMessageDto brokerMessageDto, String routingKey, int broker_count){
        SMSMessageDto smsMessageDto = brokerMessageDto.getSmsMessageDto();
        Message message = brokerMessageDto.getMessage();
        List<Contact> contacts = brokerMessageDto.getContacts();

        String brokerName = routingKey.split("\\.")[2].toUpperCase();
        Broker broker = brokerRepository.findByName(brokerName);

        long messageId = brokerMessageDto.getMessage().getId();

        HashMap<String, String> rMessageResultMap = new HashMap<>();

        for (int i = 0; i < broker_count; i++) {
            smsMessageDto.setTo(contacts.get(0).getPhoneNumber()); // TODO 테스트를 위해 0으로 설정, 추후에 i로 변경해야 함
            Contact contact = contacts.get(0); // TODO 테스트를 위해 0으로 설정, 추후에 i로 변경해야 함

            MessageResultDto messageResultDto = MessageResultDto.builder()
                    .messageId(messageId)
                    .contactId(contact.getId())
                    .brokerId(broker.getId())
                    .messageStatus(MessageStatus.PENDING)
                    .build();

            RMessageResult rMessageResult = RMessageResult.builder()
                    .messageId(message.getId())
                    .brokerId(broker.getId())
                    .contactId(contact.getId())
                    .messageStatus(MessageStatus.PENDING)
                    .build();

            rMessageResultMap.put(String.valueOf(i), convertToJson(rMessageResult));

            BrokerSendMessageDto brokerSendMessageDto = new BrokerSendMessageDto(smsMessageDto, messageResultDto);

            // AMQP Message Builder
            org.springframework.amqp.core.Message amqpMessage = MessageBuilder
                    .withBody(convertToJson(brokerSendMessageDto).getBytes())
                            .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                                    .build();

            rabbitTemplate.convertAndSend(SMS_EXCHANGE_NAME, routingKey, amqpMessage);

            log.info((i + 1) + " 번째 메시지가 전송되었습니다 - " + routingKey);
        }

        // RedisTemplate Map 자료구조 사용 (속도 더 빠름)
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(messageId + "." + brokerName, rMessageResultMap);

        System.out.println("종료");
    }





    public String sendKakaoMessage(BrokerKakaoMessageDto brokerKakaoMessageDto) {
        // 시간 측정
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Member member = brokerKakaoMessageDto.getMember();

        // KT, LG, SKT 순으로 정렬해서 가져옴
        List<MessageRule> messageRules = messageRuleRepository.findAllByMember(member);

        int count = brokerKakaoMessageDto.getCount();

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
        sendMsgToBroker(brokerMessageDto, KT_WORK_ROUTING_KEY, broker_count[0]);

        // lg
        sendMsgToBroker(brokerMessageDto, LG_WORK_ROUTING_KEY, broker_count[1]);

        // skt
        sendMsgToBroker(brokerMessageDto, SKT_WORK_ROUTING_KEY, broker_count[2]);

        // 시간 측정 결과
        stopWatch.stop();
        String processTime = String.valueOf(stopWatch.getTime());
        log.info("Process Time: {} ", processTime);
        return processTime;
    }

    private void sendKakaoMsgToBroker(BrokerMessageDto brokerMessageDto, String routingKey, int broker_count){
        SMSMessageDto smsMessageDto = brokerMessageDto.getSmsMessageDto();
        Message message = brokerMessageDto.getMessage();
        List<Contact> contacts = brokerMessageDto.getContacts();

        String brokerName = routingKey.split("\\.")[2].toUpperCase();
        Broker broker = brokerRepository.findByName(brokerName);

        long messageId = brokerMessageDto.getMessage().getId();

        HashMap<String, String> rMessageResultMap = new HashMap<>();

        for (int i = 0; i < broker_count; i++) {
            smsMessageDto.setTo(contacts.get(0).getPhoneNumber()); // TODO 테스트를 위해 0으로 설정, 추후에 i로 변경해야 함
            Contact contact = contacts.get(0); // TODO 테스트를 위해 0으로 설정, 추후에 i로 변경해야 함

            MessageResultDto messageResultDto = MessageResultDto.builder()
                    .messageId(messageId)
                    .contactId(contact.getId())
                    .brokerId(broker.getId())
                    .messageStatus(MessageStatus.PENDING)
                    .build();

            RMessageResult rMessageResult = RMessageResult.builder()
                    .messageId(message.getId())
                    .brokerId(broker.getId())
                    .contactId(contact.getId())
                    .messageStatus(MessageStatus.PENDING)
                    .build();

            rMessageResultMap.put(String.valueOf(i), convertToJson(rMessageResult));

            BrokerSendMessageDto brokerSendMessageDto = new BrokerSendMessageDto(smsMessageDto, messageResultDto);

            // AMQP Message Builder
            org.springframework.amqp.core.Message amqpMessage = MessageBuilder
                    .withBody(convertToJson(brokerSendMessageDto).getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            rabbitTemplate.convertAndSend(SMS_EXCHANGE_NAME, routingKey, amqpMessage);

            log.info((i + 1) + " 번째 메시지가 전송되었습니다 - " + routingKey);
        }

        // RedisTemplate Map 자료구조 사용 (속도 더 빠름)
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(messageId + "." + brokerName, rMessageResultMap);

        System.out.println("종료");
    }










    // Json 형태로 반환
    public String convertToJson(Object object){
        String sendMessageJson = null;
        try {
             sendMessageJson = objectMapper.writeValueAsString(object);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return sendMessageJson;
    }


}
