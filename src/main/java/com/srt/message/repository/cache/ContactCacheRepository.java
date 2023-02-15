package com.srt.message.repository.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Contact;
import com.srt.message.repository.ContactGroupRepository;
import com.srt.message.repository.ContactRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.NOT_EXIST_CONTACT;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContactCacheRepository {
    private final ObjectMapper objectMapper;

    private final RedisHashRepository<String> redisHashRepository;

    private final ContactRepository contactRepository;

    private final ContactGroupRepository contactGroupRepository;

    @Cacheable(value = "Contact", key = "#contactId")
    public Contact findContactById(long contactId){
        return contactRepository.findContactById(contactId).orElseThrow(() -> new BaseException(NOT_EXIST_CONTACT));
    }

    // 레디스에서 직접 조회 (브로커에서 메시지 저장 캐싱 용도)
    public Contact findContactByContactIdAndMessageId(long contactId, long messageId) {
        String contactKey = "message.contact." + messageId;
        String contactJson = redisHashRepository.findByContactId(contactKey, String.valueOf(contactId));

        Contact contact = null;

        try {
            if(contactJson == null) // redis expired 됐을 경우
                return null;

            contact = objectMapper.readValue(contactJson, Contact.class);
        }catch(JsonProcessingException e){
            e.printStackTrace();
        }

        return contact;
    }
}
