package com.srt.message.service;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import com.srt.message.dto.contact.ContactDTO;
import com.srt.message.dto.contact.get.GetContactAllRes;
import com.srt.message.dto.contact.get.GetContactRes;
import com.srt.message.dto.contact.get.GetGroupContactRes;
import com.srt.message.dto.contact.patch.PatchContactReq;
import com.srt.message.dto.contact.patch.PatchContactRes;
import com.srt.message.dto.contact.post.PostContactReq;
import com.srt.message.dto.contact.post.PostContactRes;
import com.srt.message.repository.ContactRepository;
import com.srt.message.repository.ContactGroupRepository;
import com.srt.message.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {
    @InjectMocks
    private ContactService contactService;

    @Mock
    private ContactRepository contactRepository;
    @Mock
    private ContactGroupRepository contactGroupRepository;
    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private ContactGroup contactGroup;
    private Contact contact;

    @BeforeEach
    void setUp(){
        this.member = Member.builder().id(1).email("forceTlight@gmail.com").
                password("1q2w3e4r!").build();

        this.contactGroup = ContactGroup.builder().id(1).member(member).name("카카오").build();

        this.contact = Contact.builder().id(1).member(member).phoneNumber("01012341234").contactGroup(contactGroup)
                .memo("쭈쭈").build();
    }

    @DisplayName("연락처 찾기")
    @Test
    void findContactById_Success(){
        // given
        given(contactRepository.findByIdAndStatus(eq(contact.getId()), eq(BaseStatus.ACTIVE))).willReturn(Optional.ofNullable(contact));

        // when
        ContactDTO contactDTO = contactService.findContactById(contact.getId());

        // then
        assertThat(contactDTO.getPhoneNumber()).isEqualTo(contact.getPhoneNumber());
        assertThat(contactDTO.getGroupName()).isEqualTo(contact.getContactGroup().getName());
    }

    @DisplayName("연락처 추가")
    @Test
    void saveContact_Success(){
        // given
        PostContactReq request = PostContactReq.builder().contactGroupId(1L)
                .phoneNumber("01012341234").memo("쭈쭈").build();
        given(memberRepository.findById(any())).willReturn(Optional.ofNullable(member));
        given(contactGroupRepository.findById(any())).willReturn(Optional.ofNullable(contactGroup));

        doReturn(contact).when(contactRepository).save(any(Contact.class));

        // when
        PostContactRes response = contactService.saveContact(request, 1L);

        // then
        assertThat(response.getContactGroupId()).isEqualTo(1L);
        assertThat(response.getPhoneNumber()).isEqualTo("01012341234");

        // verify
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @DisplayName("연락처 수정")
    @Test
    void editContact_Success(){
        // given
        PatchContactReq request = PatchContactReq.builder().contactId(1L).contactGroupId(1L)
                .phoneNumber("01010101010").memo("진우").build();

        given(contactRepository.findByIdAndStatus(eq(contact.getId()), eq(BaseStatus.ACTIVE))).willReturn(Optional.ofNullable(contact));
        given(contactGroupRepository.findByIdAndStatus(eq(contactGroup.getId()), eq(BaseStatus.ACTIVE))).willReturn(Optional.ofNullable(contactGroup));

        // when
        PatchContactRes response = contactService.editContact(request, member.getId());

        // then
        assertThat(response.getPhoneNumber()).isEqualTo("01010101010");
        assertThat(response.getMemo()).isEqualTo("진우");
    }

    @DisplayName("연락처 삭제")
    @Test
    void deleteContact_Success(){
        // given
        contact.changeStatusInActive();
        given(contactRepository.findByIdAndStatus(eq(contact.getId()), eq(BaseStatus.ACTIVE))).willReturn(Optional.ofNullable(contact));

        doReturn(contact).when(contactRepository).save(any(Contact.class));

        // when
        contactService.deleteContact(1L, 1L);

        // verify
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @DisplayName("연락처 그룹으로 필터")
    @Test
    void filterContactByGroup_Success(){
        // given
        given(contactRepository.findByContactGroupIdAndMemberIdAndStatus(eq(contact.getId()), eq(member.getId()),eq(BaseStatus.ACTIVE)))
                .willReturn(Arrays.asList(contact));

        // when
        List<GetGroupContactRes> response = contactService.filterContactByGroup(contactGroup.getId(), member.getId());

        // then
        assertThat(response.get(0).getContactId()).isEqualTo(contact.getId());
    }

    @DisplayName("전체 연락처 페이징 조회")
    @Test
    void getMemberContact_Success(){
        // given
        int page = 1;
        PageRequest pageRequest = PageRequest.of(page-1, 5, Sort.by("id").descending());
        Page<Contact> contactPage = new PageImpl<>(Arrays.asList(contact), pageRequest, 0); // 페이지 인스턴스 객체 생성

        given(contactRepository.findAllContact(any(), eq(BaseStatus.ACTIVE), eq(pageRequest)))
                .willReturn(contactPage);

        // when
        PageResult<GetContactRes> response = contactService.getMemberContact(member.getId(), 1);

        // then
        assertThat(response.getDtoList().get(0).getContactId()).isEqualTo(contact.getId());
    }

    @DisplayName("연락처 전체 조회")
    @Test
    void getMemberContactAll(){
        // when
        doReturn(Arrays.asList(contact, contact, contact)).when(contactRepository).findByMemberIdAndStatus(member.getId(), BaseStatus.ACTIVE);

        // given
        GetContactAllRes response = contactService.getMemberContactAll(member.getId());

        // then
        assertThat(response.getContacts().size()).isEqualTo(3);
    }
}
