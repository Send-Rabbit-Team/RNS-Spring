package com.srt.message.service.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.type.MessageType;
import com.srt.message.domain.*;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.message.SMSMessageDto;
import com.srt.message.repository.BrokerRepository;
import com.srt.message.repository.MessageRuleRepository;
import com.srt.message.repository.ReserveMessageRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import com.srt.message.repository.redis.RedisListRepository;
import com.srt.message.service.message.BrokerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;

import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.SMS_EXCHANGE_NAME;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BrokerServiceTest {
    @InjectMocks
    private BrokerService brokerService;

    @Mock
    private RedisHashRepository redisHashRepository;
    @Mock
    private RedisListRepository redisListRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private BrokerRepository brokerRepository;
    @Mock
    private MessageRuleRepository messageRuleRepository;

    @Mock
    private ReserveMessageRepository reserveMessageRepository;

    @Spy
    private ObjectMapper objectMapper;

    private Member member;
    private Message message;
    private SenderNumber senderNumber;
    private BrokerMessageDto brokerMessageDto;
    private List<Contact> contacts;
    private List<Broker> brokers;

    @BeforeEach
    void setUp(){
        // Member
        member = Member.builder()
                .id(1).email("kakao@gmail.com").phoneNumber("01012341234")
                .build();

        // SenderNumber
        senderNumber = SenderNumber.builder()
                .id(1).phoneNumber("01012341234").blockNumber("01099999999").member(member).memo("형준 핸드폰")
                .build();

        // SmsMessage Dto
        SMSMessageDto smsMessageDto = SMSMessageDto.builder()
                .subject("[카카오 엔터프라이즈]")
                .content("2023년 사업 계획서입니다.")
                .from("01012341234")
                .messageType(MessageType.SMS)
                .build();

        // Message
        message = Message.builder()
                .id(1)
                .subject(smsMessageDto.getSubject())
                .content(smsMessageDto.getContent())
                .senderNumber(senderNumber)
                .member(member)
                .messageType(smsMessageDto.getMessageType())
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

        // Broker List
        Broker brokerKT = Broker.builder()
                .id(1).name("kt")
                .build();
        Broker brokerSKT = Broker.builder()
                .id(2).name("skt")
                .build();
        Broker brokerLG = Broker.builder()
                .id(3).name("lg")
                .build();
        brokers = Arrays.asList(brokerKT, brokerLG, brokerSKT);

        // BrokerMessageDto
        brokerMessageDto = BrokerMessageDto.builder()
                .smsMessageDto(smsMessageDto)
                .message(message)
                .contacts(contacts)
                .member(member)
                .build();
    }

    @DisplayName("브로커로 메시지 전송")
    @Test
    void sendSmsMessage_Success(){
        // given
        doReturn(brokers).when(brokerRepository).findAll();

        doNothing().when(redisListRepository).rightPushAll(any(), any(), anyInt()); // 입시 값 저장
        doNothing().when(redisListRepository).remove(any()); // 임시 값 제거
        doNothing().when(redisHashRepository).saveAll(any(), any()); // 상태 값 저장

        doNothing().when(rabbitTemplate).convertAndSend(eq(SMS_EXCHANGE_NAME), anyString(), any(Object.class));

        // when
        brokerService.sendSmsMessage(brokerMessageDto);

        // verify
        verify(redisListRepository, times(2)).rightPushAll(any(), any(), anyInt());
        verify(redisListRepository, times(2)).remove(any());
        verify(redisHashRepository, times(1)).saveAll(any(), any());

        verify(rabbitTemplate, times(contacts.size())).convertAndSend(eq(SMS_EXCHANGE_NAME), anyString(), any(Object.class));

    }
}