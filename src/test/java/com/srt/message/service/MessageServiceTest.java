package com.srt.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.type.MessageType;
import com.srt.message.domain.*;
import com.srt.message.dto.message.SMSMessageDto;
import com.srt.message.dto.message.post.PostSendMessageReq;
import com.srt.message.repository.*;
import com.srt.message.service.rabbit.BrokerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.srt.message.config.status.BaseStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    // MessageService Mock
    @InjectMocks
    private MessageService messageService;
    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private SenderNumberRepository senderNumberRepository;
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private MessageRepository messageRepository;

    // BrokerService Mock
    @Mock
    private BrokerService brokerService;

    // ReserveMessageServiceMock
    @Mock
    private ReserveMessageService reserveMessageService;

    private Member member;
    private List<Contact> contacts;
    private SenderNumber senderNumber;
    private PostSendMessageReq postSendMessageReq;

    @BeforeEach
    void setUp(){
        // Member
        member = Member.builder()
                .id(1).email("kakao@gmail.com").phoneNumber("01012341234")
                .build();

        // Contact Group
        ContactGroup contactGroup = ContactGroup.builder()
                .id(1).member(member).name("Send Rabbit Team")
                .build();

        // Contact List
        Contact contact1 = Contact.builder()
                .id(1).phoneNumber("01012341234").memo("형준").member(member)
                .build();
        Contact contact2 = Contact.builder()
                .id(2).phoneNumber("01012121212").memo("영주").member(member)
                .build();
        Contact contact3 = Contact.builder()
                .id(3).phoneNumber("01013131313").memo("예나").member(member)
                .build();
        contacts = Arrays.asList(contact1, contact2, contact3);

        // SenderNumber
        senderNumber = SenderNumber.builder()
                .id(1).phoneNumber("01012341234").blockNumber("01099999999").member(member).memo("형준 핸드폰")
                .build();

        // PostSendMessageReq
        SMSMessageDto smsMessageDto = SMSMessageDto.builder()
                .subject("[카카오 엔터프라이즈]")
                .content("2023년 사업 계획서입니다.")
                .from("01012341234")
                .messageType(MessageType.SMS)
                .build();

        List<String> receivers = Arrays.asList("01012341234", "01010101010", "01012121212");
        postSendMessageReq = PostSendMessageReq.builder().message(smsMessageDto)
                .receivers(receivers).build();

    }

    @DisplayName("일반 메시지 발송")
    @Test
    void sendMessageToBroker_Success(){
        // given
        doReturn(Optional.ofNullable(member)).when(memberRepository).findById(any());
        doReturn(contacts).when(contactRepository).findByPhoneNumberIn(any());
        doReturn(Optional.ofNullable(senderNumber)).when(senderNumberRepository)
                .findByPhoneNumberAndStatus(any(), eq(ACTIVE));

        doReturn("성공").when(brokerService).sendSmsMessage(any());

        // when
        String response = messageService.sendMessageToBroker(postSendMessageReq, member.getId());

        // then
        assertThat(response).isEqualTo("성공");
    }

    @DisplayName("예약 메시지 발송")
    @Test
    void sendMessageToBroker_Reserve_Success(){
        // given
        postSendMessageReq.getMessage().setCronExpression("0/1 * * * * ?");
        postSendMessageReq.getMessage().setCronText("1초");

        doReturn(Optional.ofNullable(member)).when(memberRepository).findById(any());
        doReturn(contacts).when(contactRepository).findByPhoneNumberIn(any());
        doReturn(Optional.ofNullable(senderNumber)).when(senderNumberRepository)
                .findByPhoneNumberAndStatus(any(), eq(ACTIVE));

        doReturn("성공").when(reserveMessageService).reserveSmsMessage(any());

        // when
        String response = messageService.sendMessageToBroker(postSendMessageReq, member.getId());

        // then
        assertThat(response).isEqualTo("성공");
    }
}