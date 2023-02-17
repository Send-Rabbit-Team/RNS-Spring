package com.srt.message.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.dto.message.BrokerSendMessageDto;
import com.srt.message.repository.BlockRepository;
import com.srt.message.repository.BrokerRepository;
import com.srt.message.repository.MessageResultRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import com.srt.message.repository.redis.RedisListRepository;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.message.SMSMessageDto;
import com.srt.message.dto.message_result.MessageResultDto;
import com.srt.message.repository.MessageRuleRepository;
import com.srt.message.service.PointService;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.srt.message.config.status.BaseStatus.ACTIVE;
import static com.srt.message.dlx.DlxProcessingErrorHandler.MESSAGE_BROKER_DEAD_COUNT;
import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.SMS_EXCHANGE_NAME;


@Log4j2
@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class BrokerService {
    private final int TMP_MESSAGE_DURATION = 5 * 60;
    private final int VALUE_MESSAGE_DURATION = 10 * 60;

    private final ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Contact> redisTemplate;

    private final BrokerCacheService brokerCacheService;
    private final PointService pointService;

    private final RedisHashRepository redisHashRepository;
    private final RedisListRepository redisListRepository;

    private final BlockRepository blockRepository;

    private final BrokerRepository brokerRepository;
    private final MessageRuleRepository messageRuleRepository;
    private final MessageResultRepository messageResultRepository;

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

        String valueKey = "message.value." + message.getId(); // tmp에서 TTL 만료됐을 경우 value값 꺼내오는 용도
        redisListRepository.rightPushAll(valueKey, rMessageResultDtos, VALUE_MESSAGE_DURATION);

        Member member = brokerMessageDto.getMember();

        // 수신 차단 처리
        String senderPhoneNumber = brokerMessageDto.getMessage().getSenderNumber().getPhoneNumber();
        List<Contact> blockContacts = blockRepository.findContactList(contacts, senderPhoneNumber, ACTIVE);
        for (Contact blockContact : blockContacts) {
            for (Contact contact : contacts) {
                if (contact == blockContact) {
                    MessageResult messageResult = MessageResult.builder()
                            .message(message)
                            .contact(blockContact)
                            .messageStatus(MessageStatus.FAIL)
                            .description("수신 차단")
                            .build();

                    // 환불
                    int refundSmsPoint = pointService.refundMessagePoint(member, 1, message.getMessageType());
                    messageResult.addDescription(refundSmsPoint + " 문자당근 환불");

                    messageResultRepository.save(messageResult);
                    break;
                }
            }
        }

        // 브로커 비율 설정
        Map<Long, String> brokerMap = new HashMap<>();
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

        ArrayList<BrokerWeight<Broker>> brokerWeights = new ArrayList<>();
        for (MessageRule messageRule : messageRules) {
            brokerWeights.add(new BrokerWeight<>(messageRule.getBroker(), messageRule.getBrokerRate()));

            Broker broker = messageRule.getBroker();
            brokerMap.put(broker.getId(), broker.getName().toLowerCase());
        }

        BrokerPool<Broker> brokerPool = new BrokerPool<>(brokerWeights);

        HashMap<String, String> rMessageResultMap = new HashMap<>();
        HashMap<String, String> contactMap = new HashMap<>();

        // 상태 DB에 저장하기
        for (int i = 0; i < contacts.size(); i++) {
            if (blockContacts.contains(contacts.get(i))) // 수신 차단 된 번호면 스킵
                continue;

            Broker broker = (Broker) brokerPool.getNext().getBroker();
            smsMessageDto.setTo(contacts.get(i).getPhoneNumber());

            // Redis에서 MessageResultDTO 꺼내오기
            MessageResultDto messageResultDto = messageResultDtos.get(i);
            messageResultDtos.get(i).setBrokerId(broker.getId());

            // 연락처 캐싱용
            Contact contact = contacts.get(i);
            contactMap.put(String.valueOf(contact.getId()), convertToJson(contact));

            // 상태 값 저장
            RMessageResult rMessageResult = MessageResultDto.toRMessageResult(messageResultDto);
            rMessageResultMap.put(rMessageResult.getId(), convertToJson(rMessageResult));
        }
        String contactKey = "message.contact." + message.getId();
        redisHashRepository.saveContactAll(contactKey, contactMap);

        String statusKey = "message.status." + message.getId();
        redisHashRepository.saveAll(statusKey, rMessageResultMap);

        // 각 중개사 비율에 맞게 보내기
        for (int i = 0; i < contacts.size(); i++) {
            MessageResultDto messageResultDto = messageResultDtos.get(i);
            BrokerSendMessageDto brokerSendMessageDto = new BrokerSendMessageDto(smsMessageDto, messageResultDto);

            long brokerId = messageResultDto.getBrokerId();
            String routingKey = "sms.work." + brokerMap.get(brokerId);

            // AMQP Message Builder
            org.springframework.amqp.core.Message amqpMessage = MessageBuilder
                    .withBody(convertToJson(brokerSendMessageDto).getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            rabbitTemplate.convertAndSend(SMS_EXCHANGE_NAME, routingKey, amqpMessage);

            log.info((i + 1) + " 번째 메시지가 전송되었습니다 - " + routingKey);
        }

        // 임시 저장된 값 제거
        redisListRepository.remove(tmpKey);
        redisListRepository.remove(valueKey);

        // 시간 측정 결과
        stopWatch.stop();
        String processTime = String.valueOf(stopWatch.getTime());
        log.info("Process Time: {} ", processTime);
        return processTime;
    }

    // 메시지 발송 실패 처리
    public void processMessageFailure(String brokerName, MessageResultDto messageResultDto) {
        messageResultDto.setRetryCount(MESSAGE_BROKER_DEAD_COUNT);
        brokerCacheService.saveMessageResultFailure(messageResultDto, brokerName);

        log.warn(brokerName + " broker got dead letter - {}", messageResultDto);
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
