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

    // Broker ???????????? ????????? ??????
    public String sendSmsMessage(BrokerMessageDto brokerMessageDto) {
        // ?????? ??????
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        this.smsMessageDto = brokerMessageDto.getSmsMessageDto();
        this.message = brokerMessageDto.getMessage();
        this.contacts = brokerMessageDto.getContacts();

        // Redis??? ?????? ?????? (????????? ?????? ??????)
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

        String valueKey = "message.value." + message.getId(); // tmp?????? TTL ???????????? ?????? value??? ???????????? ??????
        redisListRepository.rightPushAll(valueKey, rMessageResultDtos, VALUE_MESSAGE_DURATION);

        Member member = brokerMessageDto.getMember();

        // ?????? ?????? ??????
        String senderPhoneNumber = brokerMessageDto.getMessage().getSenderNumber().getPhoneNumber();
        List<Contact> blockContacts = blockRepository.findContactList(contacts, senderPhoneNumber, ACTIVE);
        for (Contact blockContact : blockContacts) {
            for (Contact contact : contacts) {
                if (contact.getPhoneNumber().equals(blockContact.getPhoneNumber())) {
                    MessageResult messageResult = MessageResult.builder()
                            .message(message)
                            .contact(blockContact)
                            .messageStatus(MessageStatus.FAIL)
                            .description("?????? ??????")
                            .build();

                    // ??????
                    int refundSmsPoint = pointService.refundMessagePoint(member, 1, message.getMessageType());
                    messageResult.addDescription(refundSmsPoint + " ???????????? ??????");

                    contacts.remove(contact);
                    messageResultRepository.save(messageResult);
                    break;
                }
            }
        }

        // ????????? ?????? ??????
        Map<Long, String> brokerMap = new HashMap<>();
        List<MessageRule> messageRules = messageRuleRepository.findAllByMember(member);
        if (messageRules.isEmpty()) { // ?????? ????????? ?????? ????????? ??????
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

        // ?????? DB??? ????????????
        for (int i = 0; i < contacts.size(); i++) {
            if (blockContacts.contains(contacts.get(i))) // ?????? ?????? ??? ????????? ??????
                continue;

            Broker broker = (Broker) brokerPool.getNext().getBroker();
            smsMessageDto.setTo(contacts.get(i).getPhoneNumber());

            // Redis?????? MessageResultDTO ????????????
            MessageResultDto messageResultDto = messageResultDtos.get(i);
            messageResultDtos.get(i).setBrokerId(broker.getId());

            // ????????? ?????????
            Contact contact = contacts.get(i);
            contactMap.put(String.valueOf(contact.getId()), convertToJson(contact));

            // ?????? ??? ??????
            RMessageResult rMessageResult = MessageResultDto.toRMessageResult(messageResultDto);
            rMessageResultMap.put(rMessageResult.getId(), convertToJson(rMessageResult));
        }
        String contactKey = "message.contact." + message.getId();
        redisHashRepository.saveContactAll(contactKey, contactMap);

        String statusKey = "message.status." + message.getId();
        redisHashRepository.saveAll(statusKey, rMessageResultMap);

        // ??? ????????? ????????? ?????? ?????????
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

            log.info((i + 1) + " ?????? ???????????? ????????????????????? - " + routingKey);
        }

        // ?????? ????????? ??? ??????
        redisListRepository.remove(tmpKey);
        redisListRepository.remove(valueKey);

        // ?????? ?????? ??????
        stopWatch.stop();
        String processTime = String.valueOf(stopWatch.getTime());
        log.info("Process Time: {} ", processTime);
        return processTime;
    }

    // ????????? ?????? ?????? ??????
    public void processMessageFailure(String brokerName, MessageResultDto messageResultDto) {
        messageResultDto.setRetryCount(MESSAGE_BROKER_DEAD_COUNT);
        brokerCacheService.saveMessageResultFailure(messageResultDto, brokerName);

        log.warn(brokerName + " broker got dead letter - {}", messageResultDto);
    }

    // Json ????????? ??????
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
