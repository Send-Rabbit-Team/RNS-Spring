package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import com.srt.message.dto.contact_group.ContactGroupDTO;
import com.srt.message.dto.contact_group.patch.PatchContactGroupReq;
import com.srt.message.dto.contact_group.patch.PatchContactGroupRes;
import com.srt.message.dto.contact_group.post.PostContactGroupReq;
import com.srt.message.dto.contact_group.post.PostContactGroupRes;
import com.srt.message.repository.ContactGroupRepository;
import com.srt.message.repository.ContactRepository;
import com.srt.message.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.srt.message.config.response.BaseResponseStatus.*;
import static com.srt.message.config.response.BaseResponseStatus.NOT_EXIST_CONTACT_NUMBER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContactGroupService {

    private ContactGroupRepository contactGroupRepository;

    private ContactGroupDTO contactGroupDTO;

    private MemberRepository memberRepository;

    private ContactRepository contactRepository;


    // 그룹 저장
    @Transactional(readOnly = false)
    public PostContactGroupRes saveContactGroup(PostContactGroupReq req, long memberId){
        String name = req.getName();

        // 그룹 기존 등록 여부
        if(contactGroupRepository.findByName(name).isPresent())
            throw new BaseException(ALREADY_EXIST_GROUP);

        // 멤버 존재 여부
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new BaseException(NOT_EXIST_MEMBER));

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

        // 연락처 수정
        ContactGroup editedContactGroup = contactGroup.editContactGroup(patchContactGroupReq);
        contactGroupRepository.save(editedContactGroup);
        return PatchContactGroupRes.toDto(editedContactGroup);
    }

    // 그룹 삭제
    @Transactional(readOnly = false)
    public void deleteContactGroup(long contactGroupId, long memberId){
        ContactGroup contactGroup = getExistContactGroup(contactGroupId);
        checkMatchMember(contactGroup, memberId);

        // 연락처 삭제
        contactGroup.changeStatusInActive();
    }

    // 편의 메서드
    public void checkMatchMember(ContactGroup contactGroup, long memberId){
        if(contactGroup.getMember().getId() != memberId)
            throw new BaseException(NOT_MATCH_MEMBER);
    }

    public ContactGroup getExistContactGroup(long contactGroupId){
        ContactGroup contactGroup = contactGroupRepository.findById(contactGroupId)
                .orElseThrow(()-> new BaseException(NOT_EXIST_GROUP));
        return contactGroup;
    }

}
