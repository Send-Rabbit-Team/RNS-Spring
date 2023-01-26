package com.srt.message.service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RKakaoMessageResult;
import com.srt.message.repository.KakaoBrokerRepository;
import com.srt.message.repository.KakaoMessageRuleRepository;
import com.srt.message.repository.redis.RKakaoMessageResultRepository;
import com.srt.message.service.dto.message.kakao.BrokerKakaoMessageDto;
import com.srt.message.service.dto.message.kakao.BrokerSendKakaoMessageDto;
import com.srt.message.service.dto.message.kakao.KakaoMessageDto;
import com.srt.message.service.dto.message_result.KakaoMessageResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.NOT_EXIST_BROKER;
import static com.srt.message.utils.rabbitmq.RabbitKakaoUtil.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoBrokerService {

    private final int KAKAO_BROKER_SIZE = 2;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RabbitTemplate rabbitTemplate;

    private final KakaoMessageRuleRepository kakaoMessageRuleRepository;

    private final KakaoBrokerRepository kakaoBrokerRepository;

    private final RKakaoMessageResultRepository rKakaoMessageResultRepository;

    private final ObjectMapper objectMapper;

    private KakaoMessageDto kakaoMessageDto;

    private KakaoMessage kakaoMessage;

    private List<Contact> contacts;

    private int contactIdx = 0;

    public String sendKakaoMessage(BrokerKakaoMessageDto brokerKakaoMessageDto) {
        // 시간 측정
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        this.kakaoMessageDto = brokerKakaoMessageDto.getKakaoMessageDto();
        this.kakaoMessage = brokerKakaoMessageDto.getKakaoMessage();
        this.contacts = brokerKakaoMessageDto.getContacts();

        Member member = brokerKakaoMessageDto.getMember();

        List<KakaoMessageRule> messageRules = kakaoMessageRuleRepository.findAllByMember(member);

        int count = brokerKakaoMessageDto.getContacts().size();

        double value = (count / 100D);
        int[] broker_count = new int[KAKAO_BROKER_SIZE]; // ([0] CNS, [1] KE)
        int sum = 0;

        for(int i = 0; i < KAKAO_BROKER_SIZE; i++){
            broker_count[i] = !messageRules.isEmpty() ? (int)(messageRules.get(i).getBrokerRate() * value) : (int)(50 * value); // 중계사 비율 설정 안했을 경우 33%씩 분배
            sum += broker_count[i];
        }

        if (sum < count) // 비율이 안떨어질 경우, 랜덤 중계사에게 배분
            broker_count[(int)(Math.random() * 2)] += count - sum;

        // Redis에 메시지 상태 저장
        saveKakaoMessageResultByRedis(broker_count);

        contactIdx = 0;

        // cns
        sendKakaoMsgToBroker(CNS_WORK_ROUTING_KEY, broker_count[0]);

        // ke
        sendKakaoMsgToBroker(KE_WORK_ROUTING_KEY, broker_count[1]);


        // 시간 측정 결과
        stopWatch.stop();
        String processTime = String.valueOf(stopWatch.getTime());
        log.info("Process Time: {} ", processTime);
        return processTime;
    }

    private void saveKakaoMessageResultByRedis(int[] broker_count){
        HashMap<String, String> rKakaoMessageResultMap = new HashMap<>();

        for(int i = 0; i < KAKAO_BROKER_SIZE; i++){
            KakaoBroker kakaoBroker = kakaoBrokerRepository.findById((long) i+1).orElseThrow(() -> new BaseException(NOT_EXIST_BROKER));
            for(int j = contactIdx; j < contactIdx + broker_count[i]; j++){
                Contact contact = contacts.get(j);
                RKakaoMessageResult rKakaoMessageResult = RKakaoMessageResult.builder()
                        .id(String.valueOf(j))
                        .kakaoMessageId(kakaoMessage.getId())
                        .kakaoBrokerId(kakaoBroker.getId())
                        .contactId(contact.getId())
                        .messageStatus(MessageStatus.PENDING)
                        .build();

                rKakaoMessageResultMap.put(String.valueOf(i) , convertToJson(rKakaoMessageResult));
            }

            String key = kakaoMessage.getId() + "." + kakaoBroker.getName();
            rKakaoMessageResultRepository.saveAll(key, rKakaoMessageResultMap);

            contactIdx += broker_count[i];
        }
    }

    private void sendKakaoMsgToBroker(String routingKey, int broker_count){
        String brokerName = routingKey.split("\\.")[2].toUpperCase();
        KakaoBroker kakaoBroker = kakaoBrokerRepository.findByName(brokerName);

        for (int i = contactIdx; i < contactIdx + broker_count; i++) {
            kakaoMessageDto.setTo(contacts.get(i).getPhoneNumber()); // TODO 테스트를 위해 0으로 설정, 추후에 i로 변경해야 함
            Contact contact = contacts.get(i); // TODO 테스트를 위해 0으로 설정, 추후에 i로 변경해야 함

            KakaoMessageResultDto kakaoMessageResultDto = KakaoMessageResultDto.builder()
                    .messageId(kakaoMessage.getId())
                    .contactId(contact.getId())
                    .brokerId(kakaoBroker.getId())
                    .messageStatus(MessageStatus.PENDING)
                    .build();

            BrokerSendKakaoMessageDto brokerSendKakaoMessageDto = new BrokerSendKakaoMessageDto(kakaoMessageDto, kakaoMessageResultDto);

            // AMQP Message Builder
            org.springframework.amqp.core.Message amqpMessage = MessageBuilder
                    .withBody(convertToJson(brokerSendKakaoMessageDto).getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            rabbitTemplate.convertAndSend(KAKAO_WORK_EXCHANGE_NAME, routingKey, amqpMessage);

            log.info((i + 1) + " 번째 메시지가 전송되었습니다 - " + routingKey);
        }

        contactIdx += broker_count;
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
