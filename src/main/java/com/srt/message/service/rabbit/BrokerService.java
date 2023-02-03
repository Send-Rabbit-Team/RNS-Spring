package com.srt.message.service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.dto.message.BrokerSendMessageDto;
import com.srt.message.repository.BrokerRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import com.srt.message.repository.redis.RedisListRepository;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.message.SMSMessageDto;
import com.srt.message.dto.message_result.MessageResultDto;
import com.srt.message.repository.MessageRuleRepository;
import com.srt.message.service.SchedulerService;
import com.srt.message.utils.algorithm.BrokerPool;
import com.srt.message.utils.algorithm.BrokerWeight;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.JSON_PROCESSING_ERROR;
import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.SMS_EXCHANGE_NAME;


@Log4j2
@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class BrokerService {
    private final int TMP_MESSAGE_DURATION = 5 * 60;
    private final int VALUE_MESSAGE_DURATION = 10 * 60;

    private final RedisHashRepository redisHashRepository;
    private final RedisListRepository redisListRepository;

    private final RabbitTemplate rabbitTemplate;

    private final BrokerRepository brokerRepository;
    private final MessageRuleRepository messageRuleRepository;

    private final SchedulerService schedulerService;

    private final ObjectMapper objectMapper;

    private SMSMessageDto smsMessageDto;
    private Message message;
    private List<Contact> contacts;

    // Broker 서버에게 메시지 전송
    public String sendSmsMessage(BrokerMessageDto brokerMessageDto) {
        // 시간 측정
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        this.smsMessageDto = brokerMessageDto.getSmsMessageDto();
        this.message = brokerMessageDto.getMessage();
        this.contacts = brokerMessageDto.getContacts();

        // Redis에 미리 저장 (메시지 손실 방지)
        int idx = 0;
        List<String> rMessageResultDtos = new ArrayList<>();
        List<MessageResultDto> messageResultDtos = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            MessageResultDto messageResultDto = MessageResultDto.builder()
                    .rMessageResultId(String.valueOf(++idx))
                    .messageId(message.getId())
                    .contactId(contacts.get(i).getId())
                    .messageStatus(MessageStatus.PENDING)
                    .build();
            messageResultDtos.add(messageResultDto);
            rMessageResultDtos.add(convertToJson(messageResultDto));
        }
        String tmpKey = "message.tmp." + message.getId();
        redisListRepository.rightPushAll(tmpKey, rMessageResultDtos, TMP_MESSAGE_DURATION);

        String valueKey ="message.value." + message.getId(); // tmp에서 TTL 만료됐을 경우 value값 꺼내오는 용도
        redisListRepository.rightPushAll(valueKey, rMessageResultDtos, VALUE_MESSAGE_DURATION);

        Member member = brokerMessageDto.getMember();

        // 브로커 비율 설정
        List<MessageRule> messageRules = messageRuleRepository.findAllByMember(member);
        if (messageRules.isEmpty()) { // 발송 규칙을 설정 안했을 경우
            List<Broker> brokers = brokerRepository.findAll();
            for (Broker broker : brokers) {
                messageRules.add(MessageRule.builder()
                        .broker(broker)
                        .brokerRate(30)
                        .build());
            }
        }

        ArrayList<BrokerWeight> brokerWeights = new ArrayList<>();
        for (MessageRule messageRule : messageRules) {
            brokerWeights.add(new BrokerWeight(messageRule.getBroker(), messageRule.getBrokerRate()));
        }

        BrokerPool brokerPool = new BrokerPool(brokerWeights);

        HashMap<String, String> rMessageResultList = new HashMap<>();
        // 각 중개사 비율에 맞게 보내기
        for (int i = 0; i < contacts.size(); i++) {
            Broker broker = (Broker) brokerPool.getNext().getBroker();
            String routingKey = "sms.send." + broker.getName().toLowerCase();

            smsMessageDto.setTo(contacts.get(i).getPhoneNumber());

            // Redis에서 MessageResultDTO 꺼내오기
            MessageResultDto messageResultDto = messageResultDtos.get(i);
            messageResultDto.setBrokerId(broker.getId());

            // 상태 값 저장
            RMessageResult rMessageResult = MessageResultDto.toRMessageResult(messageResultDto);
            rMessageResultList.put(rMessageResult.getId(), convertToJson(rMessageResult));

            BrokerSendMessageDto brokerSendMessageDto = new BrokerSendMessageDto(smsMessageDto, messageResultDto);

            // AMQP Message Builder
            org.springframework.amqp.core.Message amqpMessage = MessageBuilder
                    .withBody(convertToJson(brokerSendMessageDto).getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            rabbitTemplate.convertAndSend(SMS_EXCHANGE_NAME, routingKey, amqpMessage);

            log.info((i + 1) + " 번째 메시지가 전송되었습니다 - " + routingKey);
        }

        String statusKey = "message.status." + message.getId();
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

    // 메시지 예약발송
    public String reserveSmsMessage(BrokerMessageDto brokerMessageDto){
        schedulerService.register(brokerMessageDto);

        return "예약성공";
    }

    // Json 형태로 반환
    public String convertToJson(Object object) {
        String sendMessageJson = null;
        try {
            sendMessageJson = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return sendMessageJson;
    }
}
