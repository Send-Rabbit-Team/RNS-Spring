package com.srt.message.service;

import com.srt.message.config.status.BaseStatus;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactGroupServiceTest {
    @InjectMocks
    private ContactGroupService contactGroupService;

    @Mock
    private ContactGroupRepository contactGroupRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ContactRepository contactRepository;

    private Member member;
    private ContactGroup contactGroup;
    private Contact contact;

    @BeforeEach
    void setUp(){
        this.member = Member.builder().id(1).email("forceTlight@gmail.com").
                password("1q2w3e4r!").build();

        this.contactGroup = ContactGroup.builder().id(1).member(member)
                .name("카카오").build();

        this.contact = Contact.builder().id(1).contactGroup(contactGroup).phoneNumber("01012341234")
                .member(member).memo("어피치").build();
    }

    @DisplayName("모든 그룹 가져오기")
    @Test
    void getAllContactGroup_Success(){
        // given
        List<ContactGroup> contactGroupList = Arrays.asList(contactGroup, contactGroup, contactGroup);

        doReturn(Optional.ofNullable(member)).when(memberRepository).findByIdAndStatus(eq(member.getId()), eq(BaseStatus.ACTIVE));
        doReturn(contactGroupList).when(contactGroupRepository).findByMemberIdAndStatus(eq(member.getId()), eq(BaseStatus.ACTIVE));

        // when
        List<ContactGroupDTO> response = contactGroupService.getAllContactGroup(member.getId());

        // then
        assertThat(response.size()).isEqualTo(3);
    }

    @DisplayName("특정 그룹 찾기")
    @Test
    void findContactGroupById_Success(){
        // given
        doReturn(Optional.ofNullable(contactGroup)).when(contactGroupRepository)
                .findByIdAndStatus(eq(contactGroup.getId()), eq(BaseStatus.ACTIVE));

        // when
        ContactGroupDTO response = contactGroupService.findContactGroupById(contactGroup.getId());

        // then
        assertThat(response.getId()).isEqualTo(contactGroup.getId());
    }

    @DisplayName("그룹 저장")
    @Test
    void saveContactGroup_Success(){
        // given
        PostContactGroupReq request = PostContactGroupReq.builder().name("가천대학교").build();

        doReturn(Optional.ofNullable(member)).when(memberRepository).findByIdAndStatus(eq(member.getId()), eq(BaseStatus.ACTIVE));
        doReturn(contactGroup).when(contactGroupRepository).save(any(ContactGroup.class));

        // when
        PostContactGroupRes response = contactGroupService.saveContactGroup(request, member.getId());

        // then
        assertThat(response.getName()).isEqualTo(request.getName());

        // verify
        verify(contactGroupRepository, times(1)).save(any(ContactGroup.class));

    }

    @DisplayName("그룹 수정")
    @Test
    void editContactGroup_Success(){
        // given
        PatchContactGroupReq request = PatchContactGroupReq.builder()
                .contactGroupId(contactGroup.getId()).name("경원대학교")
                .build();

        doReturn(Optional.ofNullable(contactGroup)).when(contactGroupRepository).findByIdAndStatus(eq(contactGroup.getId()), eq(BaseStatus.ACTIVE));

        // when
        PatchContactGroupRes response = contactGroupService.editContactGroup(request, member.getId());

        // then
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @DisplayName("그룹 삭제")
    @Test
    void deleteContactGroup(){
        // given
        doReturn(Optional.ofNullable(contactGroup)).when(contactGroupRepository)
                .findByIdAndStatus(eq(contactGroup.getId()), eq(BaseStatus.ACTIVE));
        doReturn(Arrays.asList(contact)).when(contactRepository).findByContactGroupIdAndStatus(eq(contactGroup.getId()), eq(BaseStatus.ACTIVE));

        doReturn(contact).when(contactRepository).save(any());

        // when
        contactGroupService.deleteContactGroup(contactGroup.getId(), member.getId());

        // verify
        verify(contactRepository, times(1)).save(any());
    }
}