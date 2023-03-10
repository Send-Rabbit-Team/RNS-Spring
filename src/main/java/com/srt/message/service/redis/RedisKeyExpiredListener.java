package com.srt.message.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Message;
import com.srt.message.domain.MessageResult;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.repository.MessageResultRepository;
import com.srt.message.repository.cache.ContactCacheRepository;
import com.srt.message.repository.cache.MessageCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.JSON_PROCESSING_ERROR;

@Log4j2
@Service
@RequiredArgsConstructor
public class RedisKeyExpiredListener{
    private final MessageResultRepository messageResultRepository;

    private final MessageCacheRepository messageCacheRepository;
    private final ContactCacheRepository contactCacheRepository;

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public void receiveMessage(String expiredKey) throws JsonProcessingException {
        if(expiredKey.startsWith("message.tmp")){
            String messageId = expiredKey.split("\\.")[2];
            String key = "message.value." + messageId;
            ListOperations<String, Object> listOperations = redisTemplate.opsForList();
            long count = listOperations.size(key);
            if(count == 0) // 다른 서버에서 미리 빼내갔을 경우, 종료
                return;

            List<RMessageResult> RMessageResultList = listOperations.leftPop(key, count).stream()
                    .map(m -> {
                        try {
                            return objectMapper.readValue((String)m, RMessageResult.class);
                        } catch (JsonProcessingException e) {
                            throw new BaseException(JSON_PROCESSING_ERROR);
                        }
                    }).collect(Collectors.toList());

            // 데이터베이스에 저장이 안된 로그들 Redis에서 추출해서 저장
            List<MessageResult> messageResultList = RMessageResultList.stream()
                    .filter(m -> {
                        Contact contact = contactCacheRepository.findContactById(m.getContactId());
                        Message message = messageCacheRepository.findMessageById(m.getMessageId());

                        return checkNotSaveInMessageResult(contact, message);})
                    .map(m -> {
                        Contact contact = contactCacheRepository.findContactById(m.getContactId());
                        Message message = messageCacheRepository.findMessageById(m.getMessageId());

                        return MessageResult.builder()
                                .contact(contact)
                                .message(message)
                                .messageStatus(MessageStatus.FAIL)
                                .build();
                    }).collect(Collectors.toList());

            messageResultRepository.saveAll(messageResultList);

            log.warn("Redis TTL expired event occurred - expiredKey: {}", expiredKey);
        }
    }

    public boolean checkNotSaveInMessageResult(Contact contact, Message message){
        return messageResultRepository.findByContactAndMessage(contact, message).isEmpty();
    }
}
