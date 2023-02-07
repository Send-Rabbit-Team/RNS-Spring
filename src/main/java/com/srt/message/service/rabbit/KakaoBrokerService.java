package com.srt.message.service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RKakaoMessageResult;
import com.srt.message.repository.KakaoMessageRuleRepository;
import com.srt.message.dto.kakao_message.BrokerKakaoMessageDto;
import com.srt.message.dto.kakao_message.BrokerSendKakaoMessageDto;
import com.srt.message.dto.kakao_message.KakaoMessageDto;
import com.srt.message.dto.message_result.KakaoMessageResultDto;
import com.srt.message.repository.redis.RedisHashRepository;
import com.srt.message.repository.redis.RedisListRepository;
import com.srt.message.utils.algorithm.BrokerPool;
import com.srt.message.utils.algorithm.BrokerWeight;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.srt.message.utils.rabbitmq.RabbitKakaoUtil.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoBrokerService {
    private final int TMP_MESSAGE_DURATION = 5 * 60;
    private final int VALUE_MESSAGE_DURATION = 30 * 60;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private final KakaoMessageRuleRepository kakaoMessageRuleRepository;

    private final RedisListRepository redisListRepository;
    private final RedisHashRepository redisHashRepository;

    private KakaoMessageDto kakaoMessageDto;
    private KakaoMessage kakaoMessage;
    private List<Contact> contacts;


    public String sendKakaoMessage(BrokerKakaoMessageDto brokerKakaoMessageDto) {
        // 시간 측정
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // BrokerKakaoMessageDto에서 정보 가져옴
        this.kakaoMessageDto = brokerKakaoMessageDto.getKakaoMessageDto();
        this.kakaoMessage = brokerKakaoMessageDto.getKakaoMessage();
        this.contacts = brokerKakaoMessageDto.getContacts();
        Member member = brokerKakaoMessageDto.getMember();

        // 수신자에 따라 KakaoMessageResultDto 생성
        int idx = 0;
        List<String> rKakaoMessageResultDtoList = new ArrayList<>();
        List<KakaoMessageResultDto> kakaoMessageResultDtoList = new ArrayList<>();
        for (Contact contact : contacts) {
            KakaoMessageResultDto kakaoMessageResultDto = KakaoMessageResultDto.builder()
                    .rMessageResultId(String.valueOf(++idx))
                    .messageId(kakaoMessage.getId())
                    .contactId(contact.getId())
                    .messageStatus(MessageStatus.PENDING)
                    .build();
            rKakaoMessageResultDtoList.add(convertToJson(kakaoMessageResultDto));
            kakaoMessageResultDtoList.add(kakaoMessageResultDto);
        }

        // Redis Repository에 임시 저장
        String tmpKey = "message.tmp." + kakaoMessage.getId();
        redisListRepository.rightPushAll(tmpKey, rKakaoMessageResultDtoList, TMP_MESSAGE_DURATION);

        String valueKey = "message.value." + kakaoMessage.getId();
        redisListRepository.rightPushAll(valueKey, rKakaoMessageResultDtoList, VALUE_MESSAGE_DURATION);

        // Message Rule 설정
        List<KakaoMessageRule> messageRules = kakaoMessageRuleRepository.findByMemberIdAndStatus(member.getId(), BaseStatus.ACTIVE);
        ArrayList<BrokerWeight> kakaoBrokerWeightList = new ArrayList<>();
        for (KakaoMessageRule messageRule : messageRules) {
            kakaoBrokerWeightList.add(new BrokerWeight(messageRule.getKakaoBroker(), messageRule.getBrokerRate()));
        }

        // 발송 비율에 따라 랜덤으로 발송 순서 정함
        BrokerPool brokerPool = new BrokerPool(kakaoBrokerWeightList);

        // 메시지 발송
        HashMap<String, String> rMessageResultList = new HashMap<>();
        for (int i = 0; i < contacts.size(); i++) {

            // 수신할 브로커(라우팅키)와 수신자 번호 지정
            KakaoBroker kakaoBroker = (KakaoBroker) brokerPool.getNext().getBroker();
            String routingKey = "kakao.send." + kakaoBroker.getName().toLowerCase();
            kakaoMessageDto.setTo(contacts.get(i).getPhoneNumber());

            KakaoMessageResultDto kakaoMessageResultDto = kakaoMessageResultDtoList.get(i);
            kakaoMessageResultDto.setBrokerId(kakaoBroker.getId());


            RKakaoMessageResult rKakaoMessageResult = KakaoMessageResultDto.toRMessageResult(kakaoMessageResultDto);
            rMessageResultList.put(rKakaoMessageResult.getId(), convertToJson(rKakaoMessageResult));

            // 전송할 메시지인 BrokerSendKakaoMessageDto를  MessageBuilder로 씌우기
            BrokerSendKakaoMessageDto brokerSendKakaoMessageDto = new BrokerSendKakaoMessageDto(kakaoMessageDto, kakaoMessageResultDto);
            org.springframework.amqp.core.Message amqpMessage = MessageBuilder
                    .withBody(convertToJson(brokerSendKakaoMessageDto).getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            // Routing Key를 이용해 메시지 전송
            rabbitTemplate.convertAndSend(KAKAO_WORK_EXCHANGE_NAME, routingKey, amqpMessage);
            log.info((i + 1) + " 번째 메시지가 전송되었습니다 - " + routingKey);
        }

        // 전송 결과 redis repositry에 저장
        String statusKey = "message.status." + kakaoMessage.getId();
        redisHashRepository.saveAll(statusKey, rMessageResultList);

        // 임시 저장된 값 제거
        redisListRepository.remove(tmpKey);
        redisListRepository.remove(valueKey);

        // 시간 측정 결과
        stopWatch.stop();
        String processTime = String.valueOf(stopWatch.getTime());
        log.info("Process Time: {} ", processTime);
        return processTime;
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
