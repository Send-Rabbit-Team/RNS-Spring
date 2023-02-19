//package com.srt.message.service;
//
//import com.srt.message.config.status.ReserveStatus;
//import com.srt.message.config.type.MessageType;
//import com.srt.message.domain.*;
//import com.srt.message.dto.contact.get.GetContactAllRes;
//import com.srt.message.dto.message.BrokerMessageDto;
//import com.srt.message.dto.message.SMSMessageDto;
//import com.srt.message.repository.MessageRepository;
//import com.srt.message.repository.ReserveMessageContactRepository;
//import com.srt.message.repository.ReserveMessageRepository;
//import com.srt.message.service.message.ReserveMessageService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReserveMessageServiceTest {
//    @InjectMocks
//    private ReserveMessageService reserveMessageService;
//
//    @Mock
//    private MessageRepository messageRepository;
//    @Mock
//    private ReserveMessageRepository reserveMessageRepository;
//    @Mock
//    private ReserveMessageContactRepository reserveMessageContactRepository;
//
//    @Mock
//    private SchedulerService schedulerService;
//
//    private Member member;
//    private SenderNumber senderNumber;
//    private SMSMessageDto smsMessageDto;
//    private Message message;
//    private ReserveMessage reserveMessage;
//    private List<Contact> contacts;
//
//    @BeforeEach
//    void setUp() {
//        // Member
//        member = Member.builder()
//                .id(1).email("kakao@gmail.com").name("네오")
//                .build();
//
//
//        // Message
//        smsMessageDto = SMSMessageDto.builder()
//                .subject("[카카오 엔터프라이즈]")
//                .content("2023년 사업 계획서입니다.")
//                .from("01012341234")
//                .messageType(MessageType.SMS)
//                .build();
//
//        message = Message.builder()
//                .id(1)
//                .subject(smsMessageDto.getSubject())
//                .content(smsMessageDto.getContent())
//                .senderNumber(senderNumber)
//                .member(member)
//                .messageType(smsMessageDto.getMessageType())
//                .build();
//
//        // ContactGroup
//        ContactGroup contactGroup = ContactGroup.builder()
//                .id(1L).member(member).name("카카오").build();
//
//        // Contact List
//        Contact contact1 = Contact.builder()
//                .id(1).phoneNumber("01012341234").memo("형준").member(member).contactGroup(contactGroup)
//                .build();
//        Contact contact2 = Contact.builder()
//                .id(2).phoneNumber("01012121212").memo("영주").member(member).contactGroup(contactGroup)
//                .build();
//        Contact contact3 = Contact.builder()
//                .id(3).phoneNumber("01013131313").memo("예나").member(member).contactGroup(contactGroup)
//                .build();
//        contacts = Arrays.asList(contact1, contact2, contact3);
//
//        // BrokerMessageDto
//        BrokerMessageDto brokerMessageDto = getBrokerMessageDto();
//
//        // ReserveMessage
//        reserveMessage = getReserveMessage(brokerMessageDto);
//    }
//
//    @DisplayName("메시지 예약 발송")
//    @Test
//    void reserveSmsMessage_Success() {
//        // given
//        BrokerMessageDto request = getBrokerMessageDto();
//
//        doNothing().when(schedulerService).register(any(), anyLong());
//        doReturn(reserveMessage).when(reserveMessageRepository).save(any());
//        doReturn(null).when(reserveMessageContactRepository).save(any());
//
//        // when
//        reserveMessageService.reserveSmsMessage(request);
//
//        // verify
//        verify(schedulerService, times(1)).register(request, reserveMessage.getId());
//        verify(reserveMessageContactRepository, times(contacts.size())).save(any());
//        verify(reserveMessageRepository, times(1)).save(any());
//    }
//
//    @DisplayName("메시지 예약 수신자 조회")
//    @Test
//    void getReserveMessageContacts_Success() {
//        // given
//        doReturn(Optional.ofNullable(reserveMessage)).when(reserveMessageRepository)
//                .findByMessageId(anyLong());
//        doReturn(getReserveMessageContactList()).when(reserveMessageContactRepository).findAllByReserveMessage(any());
//
//        // when
//        GetContactAllRes response = reserveMessageService.getReserveMessageContacts(1L);
//
//        // then
//        assertThat(response.getContacts().size()).isEqualTo(contacts.size());
//    }
//
//    @DisplayName("메시지 예약 취소")
//    @Test
//    void cancelReserveMessage_Success() {
//        // given
//        doReturn(Optional.ofNullable(message)).when(messageRepository).findById(anyLong());
//        doReturn(Optional.ofNullable(reserveMessage)).when(reserveMessageRepository).findByMessageId(anyLong());
//
//        doNothing().when(schedulerService).remove(anyLong());
//
//        // when
//        reserveMessageService.cancelReserveMessage(message.getId(), member.getId());
//
//        // verify
//        verify(schedulerService, times(1)).remove(reserveMessage.getId());
//    }
//
//    BrokerMessageDto getBrokerMessageDto() {
//        // SenderNumber
//        SenderNumber senderNumber = SenderNumber.builder()
//                .id(1).phoneNumber("01012341234").blockNumber("01099999999").member(member).memo("형준 핸드폰")
//                .build();
//
//        // BrokerMessageDto
//        return BrokerMessageDto.builder()
//                .smsMessageDto(smsMessageDto)
//                .message(message)
//                .contacts(contacts)
//                .member(member)
//                .build();
//    }
//
//    ReserveMessage getReserveMessage(BrokerMessageDto brokerMessageDto) {
//        SMSMessageDto messageDto = brokerMessageDto.getSmsMessageDto();
//
//        return ReserveMessage.builder()
//                .message(brokerMessageDto.getMessage())
//                .cronExpression(messageDto.getCronExpression())
//                .cronText(messageDto.getCronText())
//                .reserveStatus(ReserveStatus.PROCESSING)
//                .build();
//    }
//
//    List<ReserveMessageContact> getReserveMessageContactList() {
//        ReserveMessageContact reserveMessageContact1 = ReserveMessageContact.builder()
//                .reserveMessage(reserveMessage).contact(contacts.get(0)).build();
//        ReserveMessageContact reserveMessageContact2 = ReserveMessageContact.builder()
//                .reserveMessage(reserveMessage).contact(contacts.get(1)).build();
//        ReserveMessageContact reserveMessageContact3 = ReserveMessageContact.builder()
//                .reserveMessage(reserveMessage).contact(contacts.get(2)).build();
//
//        return Arrays.asList(reserveMessageContact1, reserveMessageContact2, reserveMessageContact3);
//    }
//}