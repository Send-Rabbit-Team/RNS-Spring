package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import com.srt.message.dto.contact.patch.PatchContactReq;
import com.srt.message.dto.contact.patch.PatchContactRes;
import com.srt.message.dto.contact.post.PostContactReq;
import com.srt.message.dto.contact.post.PostContactRes;
import com.srt.message.repository.ContactRepository;
import com.srt.message.repository.ContactGroupRepository;
import com.srt.message.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactService {

        private final ContactRepository contactRepository;

        private final ContactGroupRepository contactGroupRepository;

        private final MemberRepository memberRepository;

    @Transactional(readOnly = false)
    public PostContactRes saveContact(PostContactReq req, long memberId){
            long groupId = req.getContactGroupId();

            Member member = memberRepository.findById(memberId).get();

            ContactGroup contactGroup = contactGroupRepository.findById(groupId)
                    .orElseThrow(() -> new BaseException(NOT_EXIST_GROUP));

            //핸드폰 기존 등록 여부
            if(contactRepository.findByPhoneNumber(req.getPhoneNumber()).isPresent())
                throw new BaseException(ALREADY_EXIST_CONTACT_NUMBER);

            Contact contact = PostContactReq.toContactEntity(req, contactGroup,member);
            contactRepository.save(contact);

            return PostContactRes.toDto(contact, contactGroup);

    }

    @Transactional(readOnly = false)
    public PatchContactRes editContact(PatchContactReq patchContactReq){

        // 존재하는 연락처인지 확인
        Contact contact = contactRepository.findById(patchContactReq.getContactId())
                .orElseThrow(() -> new BaseException(NOT_EXIST_CONTACT_NUMBER));

        // 그룹 찾기
        ContactGroup contactGroup = null;
        Long contactGroupId = patchContactReq.getContactGroupId();
        if(patchContactReq.getContactGroupId() != null)
            contactGroup = contactGroupRepository.findById(contactGroupId).get();

        // 연락처 수정
        Contact editedContact = contact.editContact(patchContactReq, contactGroup);
        contactRepository.save(editedContact);

        return PatchContactRes.toDto(contact);
    }

    @Transactional(readOnly = false)
    public void deleteContact(long contactId){
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(()-> new BaseException(NOT_EXIST_CONTACT_NUMBER));

        // 연락처 삭제
        contact.changeStatusInActive();
    }
}
