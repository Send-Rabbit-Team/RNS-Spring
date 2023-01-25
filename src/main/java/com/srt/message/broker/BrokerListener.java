package com.srt.message.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.domain.Broker;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Message;
import com.srt.message.domain.MessageResult;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.repository.redis.RMessageResultRepository;
import com.srt.message.service.dto.message_result.MessageResultDto;
import com.srt.message.repository.MessageResultRepository;
import com.srt.message.repository.cache.BrokerCacheRepository;
import com.srt.message.repository.cache.ContactCacheRepository;
import com.srt.message.repository.cache.MessageCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class BrokerListener {
    private final RMessageResultRepository rMessageResultRepository;

    private final ObjectMapper objectMapper;

    private final MessageResultRepository messageResultRepository;

    private final MessageCacheRepository messageCacheRepository;
    private final ContactCacheRepository contactCacheRepository;
    private final BrokerCacheRepository brokerCacheRepository;

    private final String KT_BROKER_NAME = "kt";
    private final String SKT_BROKER_NAME = "skt";
    private final String LG_BROKER_NAME = "lg";


    // KT RESPONSE
    @RabbitListener(queues = "q.sms.kt.receive", concurrency = "3")
    public void receiveSmsKTMessage(final MessageResultDto messageResultDto){
        updateRMessageResult(messageResultDto, KT_BROKER_NAME);
        saveMessageResult(messageResultDto, KT_BROKER_NAME);
    }

    // SKT RESPONSE
    @RabbitListener(queues = "q.sms.skt.receive", concurrency = "3")
    public void receiveSmsSKTMessage(final MessageResultDto messageResultDto){
        updateRMessageResult(messageResultDto, SKT_BROKER_NAME);
        saveMessageResult(messageResultDto, SKT_BROKER_NAME);
    }

    // LG RESPONSE
    @RabbitListener(queues = "q.sms.lg.receive", concurrency = "3")
    public void receiveSmsLGMessage(final MessageResultDto messageResultDto){
        updateRMessageResult(messageResultDto, LG_BROKER_NAME);
        saveMessageResult(messageResultDto, LG_BROKER_NAME);
    }

    public void updateRMessageResult(final MessageResultDto messageResultDto, String brokerName){
        Message message = messageCacheRepository.findMessageById(messageResultDto.getMessageId());
        Broker broker = brokerCacheRepository.findBrokerById(messageResultDto.getBrokerId());
        String rMessageResultId = messageResultDto.getRMessageResultId();

        // Redis에서 상태 가져오기
        String key = message.getId() + "." + broker.getName();
        String jsonRMessageResult = rMessageResultRepository.findById(key, rMessageResultId);

        // 상태 업데이트 및 저장
        RMessageResult rMessageResult = convertToRMessageResult(jsonRMessageResult);
        rMessageResult.changeMessageStatus(messageResultDto.getMessageStatus());

        rMessageResultRepository.save(key, rMessageResultId, rMessageResult);
    }

    public void saveMessageResult(final MessageResultDto messageResultDto, String brokerName) {
        Message message = messageCacheRepository.findMessageById(messageResultDto.getMessageId());
        Contact contact = contactCacheRepository.findContactById(messageResultDto.getContactId());
        Broker broker = brokerCacheRepository.findBrokerById(messageResultDto.getBrokerId());

        MessageResult messageResult = MessageResult.builder()
                .message(message)
                .contact(contact)
                .broker(broker)
                .messageStatus(messageResultDto.getMessageStatus())
                .build();

        messageResultRepository.save(messageResult);

        log.info("MessageResult 객체가 저장되었습니다. id : {}", messageResult.getId());
    }

    public RMessageResult convertToRMessageResult(String json){
        RMessageResult rMessageResult = null;
        try {
            rMessageResult = objectMapper.readValue(json, RMessageResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return rMessageResult;
    }
}
