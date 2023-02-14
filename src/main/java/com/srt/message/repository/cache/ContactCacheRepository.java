package com.srt.message.repository.cache;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Contact;
import com.srt.message.repository.ContactGroupRepository;
import com.srt.message.repository.ContactRepository;
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
    private final ContactRepository contactRepository;

    private final ContactGroupRepository contactGroupRepository;

    @Cacheable(value = "Contact", key = "#contactId")
    public Contact findContactById(long contactId){
        return contactRepository.findContactById(contactId).orElseThrow(() -> new BaseException(NOT_EXIST_CONTACT));
    }

    @Cacheable(value ="ContactList", key = "#contactGroupId")
    public List<Contact> findContactsByContactGroupId(long contactGroupId){
        return contactRepository.findByContactGroupIdAndStatus(contactGroupId, BaseStatus.ACTIVE);
    }
}
