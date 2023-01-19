package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponseStatus;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import com.srt.message.service.dto.contact.ContactDTO;
import com.srt.message.service.dto.contact.get.GetContactAllRes;
import com.srt.message.service.dto.contact.get.GetContactRes;
import com.srt.message.service.dto.contact.get.GetGroupContactRes;
import com.srt.message.service.dto.contact.patch.PatchContactReq;
import com.srt.message.service.dto.contact.patch.PatchContactRes;
import com.srt.message.service.dto.contact.post.PostContactReq;
import com.srt.message.service.dto.contact.post.PostContactRes;
import com.srt.message.repository.ContactRepository;
import com.srt.message.repository.ContactGroupRepository;
import com.srt.message.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactService {
    private final ContactRepository contactRepository;
    private final ContactGroupRepository contactGroupRepository;
    private final MemberRepository memberRepository;


    // 연락처 찾기
    @Transactional(readOnly = false)
    public ContactDTO findContactById(long contactId){
        Contact contact = contactRepository.findByIdAndStatus(contactId,BaseStatus.ACTIVE)
                .orElseThrow(()-> new BaseException(NOT_EXIST_CONTACT_NUMBER));

        return ContactDTO.toDto(contact);
    }


    // 연락처 추가
    @Transactional(readOnly = false)
    public PostContactRes saveContact(PostContactReq req, long memberId){
        Long groupId = req.getContactGroupId();

        // 핸드폰 기존 등록 여부
        if(contactRepository.findByPhoneNumberAndStatus(req.getPhoneNumber(), BaseStatus.ACTIVE).isPresent())
            throw new BaseException(ALREADY_EXIST_CONTACT_NUMBER);

        // 멤버 존재 여부
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));

        ContactGroup contactGroup = null;
        // 그룹 존재 여부
        if(groupId != null)
            contactGroup = contactGroupRepository.findById(groupId)
                    .orElseThrow(() -> new BaseException(NOT_EXIST_GROUP));


        Contact contact = PostContactReq.toEntity(req, contactGroup, member);
        contactRepository.save(contact);

        return PostContactRes.toDto(contact, contactGroup);

    }


    // 연락처 수정
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
        contact.editContact(patchContactReq, contactGroup);

        return PatchContactRes.toDto(contact);
    }

    @Transactional(readOnly = false)
    public void deleteContact(long contactId, long memberId){
        Contact contact = getExistContact(contactId);
        checkMatchMember(contact, memberId);

        // 연락처 삭제
        contact.changeStatusInActive();
        contactRepository.save(contact);
    }

    @Transactional(readOnly = false)
    public void quitContactGroup(long contactId, long memberId){
        // 존재하는 연락처인지 확인
        Contact contact = getExistContact(contactId);

        // 연락처를 변경할 권한이 있는지 확인
        checkMatchMember(contact, memberId);

        // 존재하는 그룹인지 확인
        long groupId = contact.getContactGroup().getId();
        if (contactGroupRepository.findByIdAndStatus(groupId, BaseStatus.ACTIVE).isEmpty()) {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_GROUP);
        }

        // 연락처에 연결된 그룹 해제
        contact.quitContactGroup();
        contactRepository.save(contact);
    }

    // 연락처 검색
    public PageResult<ContactDTO, Contact> searchContact(String phoneNumber, int currentPage, long memberId) {
        PageRequest pageRequest = PageRequest.of(currentPage-1, 5, Sort.by("id").descending());
        Page<Contact> contactPage = contactRepository.findbyPhoneNumber(phoneNumber, pageRequest, memberId,BaseStatus.ACTIVE);
        Function<Contact, ContactDTO> fn = (contact -> ContactDTO.toDto(contact));
        return new PageResult<>(contactPage, fn);
    }

    // 연락처 그룹으로 필터링
    public List<GetGroupContactRes> filterContactByGroup(long groupId, long memberId){
        List<Contact> contactList = contactRepository.findByContactGroupIdAndMemberIdAndStatus(groupId, memberId, BaseStatus.ACTIVE);

        if (contactList.isEmpty())
            throw new BaseException(NOT_EXIST_CONTACT_NUMBER);

        return contactList.stream().map(contact -> GetGroupContactRes.toDto(contact)).collect(Collectors.toList());
    };

    // 편의 메서드
    public void checkMatchMember(Contact contact, long memberId){
        if(contact.getMember().getId() != memberId)
            throw new BaseException(NOT_MATCH_MEMBER);
    }

    public Contact getExistContact(long contactId){
        Contact contact = contactRepository.findByIdAndStatus(contactId,BaseStatus.ACTIVE)
                .orElseThrow(()-> new BaseException(NOT_EXIST_CONTACT_NUMBER));
        return contact;
    }

    // 전체 연락처 조회(페이징)
    public PageResult<GetContactRes, Contact> getMemberContact(long memberId, int page) {
        PageRequest pageRequest = PageRequest.of(page-1, 5, Sort.by("id").descending());
        Page<Contact> contactPage = contactRepository.findAll(memberId, BaseStatus.ACTIVE, pageRequest);
        Function<Contact, GetContactRes> fn = (contact -> GetContactRes.toDto(contact));
        return new PageResult<>(contactPage, fn);
    }

    public GetContactAllRes getMemberContactAll(long memberId){
        List<Contact> contactList = contactRepository.findByMemberIdAndStatus(memberId, BaseStatus.ACTIVE);
        GetContactAllRes getContactAllRes = GetContactAllRes.toDto(contactList);
        return getContactAllRes;
    }
}
