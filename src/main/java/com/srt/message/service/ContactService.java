package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import com.srt.message.dto.contact.ContactDTO;
import com.srt.message.dto.contact.patch.PatchContactReq;
import com.srt.message.dto.contact.patch.PatchContactRes;
import com.srt.message.dto.contact.post.PostContactReq;
import com.srt.message.dto.contact.post.PostContactRes;
import com.srt.message.repository.ContactRepository;
import com.srt.message.repository.ContactGroupRepository;
import com.srt.message.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PatchContactRes editContact(PatchContactReq patchContactReq, long memberId){
        // 존재하는 연락처인지 확인
        Contact contact = getExistContact(patchContactReq.getContactId());

        checkMatchMember(contact, memberId);

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
    public void deleteContact(long contactId, long memberId){
        Contact contact = getExistContact(contactId);

        checkMatchMember(contact, memberId);

        // 연락처 삭제
        contact.changeStatusInActive();
    }

    // 연락처 검색
    @Transactional
    public Page<ContactDTO> searchByNumber(String phoneNumber, int currentPage) {
        PageRequest pageRequest = PageRequest.of(currentPage, 10, Sort.by("id").descending());
        Page<Contact> contactList = contactRepository.findByPhoneNumberContaining(phoneNumber, pageRequest);
        Page<ContactDTO> contactListDTO = contactList.map(m-> Contact.toDto(m));

        return contactListDTO;
    }

    // 연락처 그룹으로 필터링
    @Transactional
    public Page<ContactDTO> filterByGroup(long groupId, int currentPage){
        PageRequest pageRequest = PageRequest.of(currentPage, 10, Sort.by("id").descending());
        Page<Contact> contactList = contactRepository.findByContactGroupId(groupId, pageRequest);
        Page<ContactDTO> contactListDTO = contactList.map(m->Contact.toDto(m));

        return contactListDTO;
    };


    // 모든 연락처 검색
    @Transactional
    public Page<Contact> getContactList(Pageable pageable) {
        return contactRepository.findAll(pageable);
    }

    // 편의 메서드
    public void checkMatchMember(Contact contact, long memberId){
        if(contact.getMember().getId() != memberId)
            throw new BaseException(NOT_MATCH_MEMBER);
    }

    public Contact getExistContact(long contactId){
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(()-> new BaseException(NOT_EXIST_CONTACT_NUMBER));
        return contact;
    }
}
