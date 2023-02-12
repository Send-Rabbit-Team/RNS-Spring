package com.srt.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.Broker;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Message;
import com.srt.message.domain.MessageResult;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.dto.message_result.MessageResultDto;
import com.srt.message.repository.MessageResultRepository;
import com.srt.message.repository.cache.BrokerCacheRepository;
import com.srt.message.repository.cache.ContactCacheRepository;
import com.srt.message.repository.cache.MessageCacheRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class BrokerCacheService {
    private final ObjectMapper objectMapper;

    private final BrokerCacheRepository brokerCacheRepository;
    private final MessageCacheRepository messageCacheRepository;
    private final ContactCacheRepository contactCacheRepository;

    private final RedisHashRepository redisHashRepository;

    private final MessageResultRepository messageResultRepository;


    public void updateRMessageResult(final MessageResultDto messageResultDto, String brokerName) {
        Broker broker = brokerCacheRepository.findBrokerById(messageResultDto.getBrokerId());
        String rMessageResultId = messageResultDto.getRMessageResultId();

        // Redis에서 상태 가져오기
        String statusKey = "message.status." + messageResultDto.getMessageId();

        // Redis에 해당 데이터가 없을 경우 종료
        if (!redisHashRepository.isExist(statusKey, rMessageResultId))
            return;

        String jsonRMessageResult = redisHashRepository.findById(statusKey, rMessageResultId);

        // 상태 업데이트 및 저장
        RMessageResult rMessageResult = convertToRMessageResult(jsonRMessageResult);
        rMessageResult.changeMessageStatus(MessageStatus.SUCCESS);

        redisHashRepository.update(statusKey, rMessageResultId, rMessageResult);
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

//        addMessageResultList(messageResult);
        messageResultRepository.save(messageResult);

        log.info("MessageResult 객체가 저장되었습니다. id : {}", messageResult.getId());
    }

    public RMessageResult convertToRMessageResult(String json) {
        RMessageResult rMessageResult = null;
        try {
            rMessageResult = objectMapper.readValue(json, RMessageResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return rMessageResult;
    }
}
