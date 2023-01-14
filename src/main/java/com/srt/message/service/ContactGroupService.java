package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
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

        return contactGroupRepository.findByMemberId(memberId).orElseThrow(()->new BaseException(NOT_EXIST_MEMBER))
                .stream().map(m-> ContactGroupDTO.toDTO(m,member)) .collect(Collectors.toList());
    }

    //그룹 찾기
    @Transactional(readOnly = false)
    public ContactGroupDTO findContactGroupById(long contactGroupId){
        ContactGroup contactGroup = contactGroupRepository.findById(contactGroupId)
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

        // 연락처 삭제
        contactGroup.changeStatusInActive();
        contactGroupRepository.save(contactGroup);
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

    // 그룹 조회
    public PageResult<GetContactGroupRes, ContactGroup> getMemberContactGroup(long memberId, int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 5, Sort.by("id").descending());

        // 그룹 조회
        Page<ContactGroup> contactGroupPage = contactGroupRepository.findByMemberIdAndStatus(memberId, BaseStatus.ACTIVE, pageRequest);

        // Domain -> DTO 변환 함수 생성
        Function<ContactGroup, GetContactGroupRes> fn = contactGroup -> {
            // 그룹 ID를 통해 그룹에 속한 연락처 조회
            long groupId = contactGroup.getId();
            List<Contact> contactList = contactRepository.findByContactGroupIdAndStatus(groupId, BaseStatus.ACTIVE).orElse(null);

            // Contact -> ContactDTO로 변환
            List<ContactDTO> contactDTOList = new ArrayList<>();
            for (Contact contact : contactList) {
                contactDTOList.add(ContactDTO.toDto(contact));
            }

            return GetContactGroupRes.toDto(contactGroup, contactDTOList);
        };
        return new PageResult<>(contactGroupPage, fn);
    }

    public Member getExistMember(long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new BaseException(NOT_EXIST_MEMBER));
        return member;

    }
}
