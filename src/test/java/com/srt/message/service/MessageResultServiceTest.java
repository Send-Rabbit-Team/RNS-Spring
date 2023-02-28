package com.srt.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.config.type.MessageType;
import com.srt.message.domain.*;
import com.srt.message.dto.message.get.GetMessageRes;
import com.srt.message.dto.message_result.get.GetListMessageResultRes;
import com.srt.message.repository.MessageRepository;
import com.srt.message.repository.MessageResultRepository;
import com.srt.message.repository.cache.BrokerCacheRepository;
import com.srt.message.repository.cache.ContactCacheRepository;
import com.srt.message.repository.cache.MessageCacheRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import com.srt.message.service.message.MessageResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MessageResultServiceTest {
    @InjectMocks
    private MessageResultService messageResultService;

    @Spy
    private ObjectMapper objectMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private MessageResultRepository messageResultRepository;

    @Mock
    private MessageCacheRepository messageCacheRepository;
    @Mock
    private BrokerCacheRepository brokerCacheRepository;
    @Mock
    private ContactCacheRepository contactCacheRepository;

    @Mock
    private RedisHashRepository redisHashRepository;

    private Member member;
    private SenderNumber senderNumber;
    private Message message;
    private Broker broker;
    private Contact contact;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(1).build();

        senderNumber = SenderNumber.builder()
                .phoneNumber("01012341234").member(member).build();

        message = Message.builder()
                .id(1).subject("[새해 복 많이 받으세요]").content("올 한해도 새해 복 많이 받으세요")
                .senderNumber(senderNumber)
                .member(member)
                .messageType(MessageType.SMS)
                .build();

        broker = Broker.builder()
                .id(1).name("kt")
                .build();

        ContactGroup contactGroup = ContactGroup.builder()
                .id(1).name("카카오").member(member).build();

        contact = Contact.builder()
                .id(1).member(member).phoneNumber("01011111111").memo("네오").contactGroup(contactGroup)
                .build();
    }

    @DisplayName("발송한 메시지 페이징 조회")
    @Test
    void getAllMessages_Success() {
        // given
        PageRequest pageRequest = getPageRequest();
        doReturn(getMessageResPageResult(pageRequest)).when(messageRepository).findAllMessage(eq(member.getId()), eq(pageRequest));

        // when
        PageResult<GetMessageRes> response = messageResultService.getAllMessages(member.getId(), 1);

        // then
        assertThat(response.getDtoList().size()).isEqualTo(3);
    }

    @DisplayName("메시지 처리 결과 모두 조회 (상태 DB로 조회)")
    @Test
    void getMessageResultsById_Redis_Success() throws JsonProcessingException {
        // given
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("1", "{\"id\":\"1\",\"messageId\":141,\"contactId\":1,\"brokerId\":1,\"messageStatus\":\"PENDING\"}");

        doReturn(statusMap).when(redisHashRepository).findAll(any());
        doReturn(message).when(messageCacheRepository).findMessageById(anyLong());
        doReturn(broker).when(brokerCacheRepository).findBrokerById(anyLong());
        doReturn(contact).when(contactCacheRepository).findContactById(anyLong());

        // when
        GetListMessageResultRes response = messageResultService.getMessageResultsById(1L);

        // then
        assertThat(response.getMessageResultRes().get(0).getContactPhoneNumber()).isEqualTo(contact.getPhoneNumber());
        assertThat(response.getMessageResultRes().get(0).getMessageStatus()).isEqualTo(MessageStatus.PENDING);
    }

    @DisplayName("메시지 처리 결과 모두 조회 (RDBMS로 조회)")
    @Test
    void getMessageResultsById_MySQL_Success() throws JsonProcessingException {
        // given
        MessageResult messageResult = MessageResult.builder()
                .id(1).message(message).broker(broker).contact(contact).messageStatus(MessageStatus.SUCCESS).build();
        doReturn(Arrays.asList(messageResult)).when(messageResultRepository).findAllByMessageIdOrderByIdDesc(anyLong());

        // when
        GetListMessageResultRes response = messageResultService.getMessageResultsById(1L);

        // then
        assertThat(response.getMessageResultRes().get(0).getContactPhoneNumber()).isEqualTo(contact.getPhoneNumber());
        assertThat(response.getMessageResultRes().get(0).getMessageStatus()).isEqualTo(MessageStatus.SUCCESS);
    }

    @DisplayName("메시지 유형 별 필터 조회")
    @Test
    void getMessagesByType_Success(){
        // given
        PageRequest pageRequest = getPageRequest();
        Page<Message> messagePage = getMessageResPageResult(pageRequest);
        doReturn(messagePage).when(messageRepository).findMessagesByMessageType(any(), anyLong(), eq(pageRequest));

        // when
        PageResult<GetMessageRes> response = messageResultService.getMessagesByType("SMS", member.getId(), 1);

        // then
        assertThat(response.getDtoList().size()).isEqualTo(3);
    }

    @DisplayName("검색 조회 (메모)")
    @Test
    void getMessageBySearching_Memo_Success(){
        // given
        PageRequest pageRequest = getPageRequest();
        Page<Message> messagePage = getMessageResPageResult(pageRequest);

        doReturn(messagePage).when(messageRepository).findByMemo(any(), anyLong(), eq(pageRequest));

        // when
        PageResult<GetMessageRes> response =
                messageResultService.getMessageBySearching("MEMO", "네오", member.getId(), 1);

        // then
        assertThat(response.getDtoList().size()).isEqualTo(3);
    }

    @DisplayName("검색 조회 (수신 번호)")
    @Test
    void getMessageBySearching_Receiver_Success(){
        // given
        PageRequest pageRequest = getPageRequest();
        Page<Message> messagePage = getMessageResPageResult(pageRequest);

        doReturn(messagePage).when(messageRepository).findByReceiveNumber(any(), anyLong(), eq(pageRequest));

        // when
        PageResult<GetMessageRes> response =
                messageResultService.getMessageBySearching("RECEIVER", "01012341234", member.getId(), 1);

        // then
        assertThat(response.getDtoList().size()).isEqualTo(3);
    }

    @DisplayName("검색 조회 (발신 번호)")
    @Test
    void getMessageBySearching_Sender_Success(){
        // given
        PageRequest pageRequest = getPageRequest();
        Page<Message> messagePage = getMessageResPageResult(pageRequest);

        doReturn(messagePage).when(messageRepository).findBySenderNumber(any(), anyLong(), eq(pageRequest));

        // when
        PageResult<GetMessageRes> response =
                messageResultService.getMessageBySearching("SENDER", "01011111111", member.getId(), 1);

        // then
        assertThat(response.getDtoList().size()).isEqualTo(3);
    }

    @DisplayName("검색 조회 (메시지 내용)")
    @Test
    void getMessageBySearching_Message_Success(){
        // given
        PageRequest pageRequest = getPageRequest();
        Page<Message> messagePage = getMessageResPageResult(pageRequest);

        doReturn(messagePage).when(messageRepository).findByMessageContent(any(), anyLong(), eq(pageRequest));

        // when
        PageResult<GetMessageRes> response =
                messageResultService.getMessageBySearching("MESSAGE", "새해", member.getId(), 1);

        // then
        assertThat(response.getDtoList().size()).isEqualTo(3);
    }

    Page<Message> getMessageResPageResult(PageRequest pageRequest) {
        Message message1 = Message.builder()
                .id(1).subject("[새해 복 많이 받으세요]").content("올 한해도 새해 복 많이 받으세요")
                .senderNumber(senderNumber)
                .member(member)
                .messageType(MessageType.SMS)
                .build();
        Message message2 = Message.builder()
                .id(2).subject("[코딩 테스트 안내]").content("금일 3시까지 프로그래머스 홈페이지로 들어오시길 바랍니다.")
                .senderNumber(senderNumber)
                .member(member)
                .messageType(MessageType.SMS)
                .build();
        Message message3 = Message.builder()
                .id(3).subject("[OOO 면접 결과 안내]").content("지원해 주셔서 감사합니다. 귀하의 면접 결과는 합격 입니다.")
                .senderNumber(senderNumber)
                .member(member)
                .messageType(MessageType.SMS)
                .build();

        message1.setCreatedAt(LocalDateTime.now());
        message2.setCreatedAt(LocalDateTime.now());
        message3.setCreatedAt(LocalDateTime.now());

        Page<Message> messagesPage = new PageImpl<>(Arrays.asList(message1, message2, message3)
                , pageRequest, 0); // 페이지 인스턴스 객체 생성

        return messagesPage;
    }

    PageRequest getPageRequest(){
        int page = 1;
        return PageRequest.of(page - 1, 10, Sort.by("id").descending());
    }
}