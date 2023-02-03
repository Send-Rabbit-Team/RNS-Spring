//package com.srt.message.listener;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.srt.message.config.status.MessageStatus;
//import com.srt.message.domain.Broker;
//import com.srt.message.domain.Contact;
//import com.srt.message.domain.Message;
//import com.srt.message.domain.MessageResult;
//import com.srt.message.domain.redis.RMessageResult;
//import com.srt.message.repository.redis.RedisHashRepository;
//import com.srt.message.dto.message_result.MessageResultDto;
//import com.srt.message.repository.MessageResultRepository;
//import com.srt.message.repository.cache.BrokerCacheRepository;
//import com.srt.message.repository.cache.ContactCacheRepository;
//import com.srt.message.repository.cache.MessageCacheRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//
//@Log4j2
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class SmsBrokerListener {
//    private final RedisHashRepository redisHashRepository;
//
//    private final ObjectMapper objectMapper;
//
//    private final MessageResultRepository messageResultRepository;
//
//    private final MessageCacheRepository messageCacheRepository;
//    private final ContactCacheRepository contactCacheRepository;
//    private final BrokerCacheRepository brokerCacheRepository;
//
//    private final String KT_BROKER_NAME = "kt";
//    private final String SKT_BROKER_NAME = "skt";
//    private final String LG_BROKER_NAME = "lg";
//
//    private ArrayList<MessageResult> messageResultList = new ArrayList<>();
//
//
//    // KT RESPONSE
//    @RabbitListener(queues = "q.sms.kt.receive", concurrency = "3", containerFactory = "prefetchContainerFactory")
//    public void receiveKTMessage(final MessageResultDto messageResultDto) {
//        updateRMessageResult(messageResultDto, KT_BROKER_NAME);
//        saveMessageResult(messageResultDto, KT_BROKER_NAME);
//    }
//
//    // SKT RESPONSE
//    @RabbitListener(queues = "q.sms.skt.receive", concurrency = "3", containerFactory = "prefetchContainerFactory")
//    public void receiveSKTMessage(final MessageResultDto messageResultDto) {
//        updateRMessageResult(messageResultDto, SKT_BROKER_NAME);
//        saveMessageResult(messageResultDto, SKT_BROKER_NAME);
//    }
//
//    // LG RESPONSE
//    @RabbitListener(queues = "q.sms.lg.receive", concurrency = "3", containerFactory = "prefetchContainerFactory")
//    public void receiveLGMessage(final MessageResultDto messageResultDto) {
//        updateRMessageResult(messageResultDto, LG_BROKER_NAME);
//        saveMessageResult(messageResultDto, LG_BROKER_NAME);
//    }
//
//    /**
//     * Dead Consumer
//     */
//    // KT
//    @RabbitListener(queues = "q.sms.kt.dead")
//    public void receiveDeadKTMessage(final MessageResultDto messageResultDto) {
//        messageResultDto.setMessageStatus(MessageStatus.FAIL);
//        updateRMessageResult(messageResultDto, KT_BROKER_NAME);
//        saveMessageResult(messageResultDto, KT_BROKER_NAME);
//
//        log.warn(KT_BROKER_NAME + " broker got dead letter - {}", messageResultDto);
//    }
//
//    // LG
//    @RabbitListener(queues = "q.sms.skt.dead")
//    public void receiveDeadSKTMessage(final MessageResultDto messageResultDto) {
//        messageResultDto.setMessageStatus(MessageStatus.FAIL);
//        updateRMessageResult(messageResultDto, LG_BROKER_NAME);
//        saveMessageResult(messageResultDto, LG_BROKER_NAME);
//
//        log.warn(LG_BROKER_NAME + " broker got dead letter - {}", messageResultDto);
//    }
//
//    // SKT
//    @RabbitListener(queues = "q.sms.lg.dead")
//    public void receiveDeadLGMessage(final MessageResultDto messageResultDto) {
//        messageResultDto.setMessageStatus(MessageStatus.FAIL);
//        updateRMessageResult(messageResultDto, SKT_BROKER_NAME);
//        saveMessageResult(messageResultDto, SKT_BROKER_NAME);
//
//        log.warn(SKT_BROKER_NAME + " broker got dead letter - {}", messageResultDto);
//    }
//
//    public void updateRMessageResult(final MessageResultDto messageResultDto, String brokerName) {
//        Broker broker = brokerCacheRepository.findBrokerById(messageResultDto.getBrokerId());
//        String rMessageResultId = messageResultDto.getRMessageResultId();
//
//        // Redis에서 상태 가져오기
//        String statusKey = "message.status." + messageResultDto.getMessageId();
//
//        // Redis에 해당 데이터가 없을 경우 종료
//        if (!redisHashRepository.isExist(statusKey, rMessageResultId))
//            return;
//
//        String jsonRMessageResult = redisHashRepository.findById(statusKey, rMessageResultId);
//
//        // 상태 업데이트 및 저장
//        RMessageResult rMessageResult = convertToRMessageResult(jsonRMessageResult);
//        rMessageResult.changeMessageStatus(MessageStatus.SUCCESS);
//
//        redisHashRepository.update(statusKey, rMessageResultId, rMessageResult);
//    }
//
//    public void saveMessageResult(final MessageResultDto messageResultDto, String brokerName) {
//        Message message = messageCacheRepository.findMessageById(messageResultDto.getMessageId());
//        Contact contact = contactCacheRepository.findContactById(messageResultDto.getContactId());
//        Broker broker = brokerCacheRepository.findBrokerById(messageResultDto.getBrokerId());
//
//        MessageResult messageResult = MessageResult.builder()
//                .message(message)
//                .contact(contact)
//                .broker(broker)
//                .messageStatus(messageResultDto.getMessageStatus())
//                .build();
//
////        addMessageResultList(messageResult);
//        messageResultRepository.save(messageResult);
//
//        log.info("MessageResult 객체가 저장되었습니다. id : {}", messageResult.getId());
//    }
//
//    public RMessageResult convertToRMessageResult(String json) {
//        RMessageResult rMessageResult = null;
//        try {
//            rMessageResult = objectMapper.readValue(json, RMessageResult.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return rMessageResult;
//    }
//
//    // MessageResult bulk insert
////    @Transactional
////    public void addMessageResultList(MessageResult messageResult){
////        if(messageResultList.size() >= 1000){
////            ArrayList<MessageResult> tmpList = (ArrayList<MessageResult>) messageResultList.clone();
////            messageResultList.clear();
////
////            messageResultRepository.saveAll(tmpList);
////            tmpList.stream().forEach(m -> log.info("MessageResult 객체가 저장되었습니다. id : {}", m.getId()));
////        }
////        messageResultList.add(messageResult);
////    }
//}
