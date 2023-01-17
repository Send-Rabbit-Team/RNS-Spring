package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponseStatus;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Contact;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import com.srt.message.dto.contact.ContactDTO;
import com.srt.message.dto.contact_group.ContactGroupDTO;
import com.srt.message.dto.contact_group.get.GetContactGroupRes;
import com.srt.message.dto.contact_group.patch.PatchContactGroupReq;
import com.srt.message.dto.contact_group.patch.PatchContactGroupRes;
import com.srt.message.dto.contact_group.post.PostContactGroupReq;
import com.srt.message.dto.contact_group.post.PostContactGroupRes;
import com.srt.message.repository.ContactGroupRepository;
import com.srt.message.repository.ContactRepository;
import com.srt.message.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.List;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContactGroupService {

    private final ContactGroupRepository contactGroupRepository;

    private final MemberRepository memberRepository;

    private final ContactRepository contactRepository;

    @Transactional(readOnly = false)
    public List<ContactGroupDTO>  getAllContactGroup(long memberId){
        Member member = getExistMember(memberId);

        List<ContactGroup> contactGroupList = contactGroupRepository.findByMemberIdAndStatus(memberId, BaseStatus.ACTIVE);

        if (contactGroupList.isEmpty())
            throw new BaseException(NOT_EXIST_MEMBER);

        return contactGroupList.stream().map(contactGroup -> ContactGroupDTO.toDTO(contactGroup,member)) .collect(Collectors.toList());
    }

    //그룹 찾기
    @Transactional(readOnly = false)
    public ContactGroupDTO findContactGroupById(long contactGroupId){
        ContactGroup contactGroup = contactGroupRepository.findByIdAndStatus(contactGroupId, BaseStatus.ACTIVE)
                .orElseThrow(()-> new BaseException(NOT_EXIST_GROUP));
        Member member = contactGroup.getMember();
        return ContactGroupDTO.toDTO(contactGroup,member);
    }

    // 그룹 저장
    @Transactional(readOnly = false)
    public PostContactGroupRes saveContactGroup(PostContactGroupReq req, long memberId){
        String name = req.getName();

        // 그룹 기존 등록 여부
        if(contactGroupRepository.findByNameAndStatus(name, BaseStatus.ACTIVE).isPresent())
            throw new BaseException(ALREADY_EXIST_GROUP);

        // 멤버 존재 여부
        Member member = getExistMember(memberId);

        ContactGroup contactGroup = ContactGroup.toEntity(req, member);
        contactGroupRepository.save(contactGroup);

        return PostContactGroupRes.toDto(contactGroup);
    }


    // 그룹 수정
    @Transactional(readOnly = false)
    public PatchContactGroupRes editContactGroup(PatchContactGroupReq patchContactGroupReq, long memberId){
        // 존재하는 그룹인지 확인
        ContactGroup contactGroup = getExistContactGroup(patchContactGroupReq.getContactGroupId());
        checkMatchMember(contactGroup, memberId);

        // 그룹 이름 수정
        contactGroup.changeName(patchContactGroupReq.getName());

        contactGroupRepository.save(contactGroup);

        return PatchContactGroupRes.toDto(contactGroup);
    }

    // 그룹 삭제
    @Transactional(readOnly = false)
    public void deleteContactGroup(long contactGroupId, long memberId){
        ContactGroup contactGroup = getExistContactGroup(contactGroupId);
        checkMatchMember(contactGroup, memberId);

        // 연락처 그룹 삭제
        contactGroup.changeStatusInActive();
        contactGroupRepository.save(contactGroup);

        // 연락처 조회
        List<Contact> contactList = contactRepository.findByContactGroupIdAndStatus(contactGroupId, BaseStatus.ACTIVE);
        if (contactList.isEmpty())
            throw new BaseException(NOT_EXIST_CONTACT_NUMBER);

        // 연락처 그룹에 연결된 연락처 해제
        for (Contact contact : contactList) {
            contact.quitContactGroup();
            contactRepository.save(contact);
        }

    }

    // 편의 메서드
    public void checkMatchMember(ContactGroup contactGroup, long memberId){
        if(contactGroup.getMember().getId() != memberId)
            throw new BaseException(NOT_MATCH_MEMBER);
    }

    public ContactGroup getExistContactGroup(long contactGroupId){
        ContactGroup contactGroup = contactGroupRepository.findByIdAndStatus(contactGroupId, BaseStatus.ACTIVE)
                .orElseThrow(()-> new BaseException(NOT_EXIST_GROUP));
        return contactGroup;
    }

    // 그룹 조회
    public PageResult<GetContactGroupRes, ContactGroup> getMemberContactGroup(long memberId, int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 5, Sort.by("id").descending());

        Page<ContactGroup> contactPage = contactGroupRepository.findByMemberIdAndStatus(memberId, BaseStatus.ACTIVE, pageRequest);

        Function<ContactGroup, GetContactGroupRes> fn = (contactGroup -> GetContactGroupRes.toDto(contactGroup));

        return new PageResult<>(contactPage, fn);
    }

    public Member getExistMember(long memberId){
        Member member = memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(()-> new BaseException(NOT_EXIST_MEMBER));
        return member;

    }
}
