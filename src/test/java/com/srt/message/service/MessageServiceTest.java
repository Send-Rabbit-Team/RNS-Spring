//package com.srt.message.service;
//
//import com.srt.message.config.status.MessageStatus;
//import com.srt.message.repository.ContactRepository;
//import com.srt.message.repository.MemberRepository;
//import com.srt.message.repository.MessageRepository;
//import com.srt.message.repository.SenderNumberRepository;
//import com.srt.message.service.dto.message.SMSMessageDto;
//import com.srt.message.service.dto.message.post.PostSendMessageReq;
//import com.srt.message.service.rabbit.BrokerService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class MessageServiceTest {
//    @Autowired
//    private MessageService messageService;
//
//    @Test
//    public void sendMessageToBroker_latency_test(){
//        // given
//        List<String> receivers = new ArrayList<>();
//        receivers.add("01093904141");
//
//        SMSMessageDto smsMessageDto = SMSMessageDto.builder()
//                .subject("[Rabbit Notification Service]")
//                .content("테스트입니다")
//                .from("01025291674")
//                .messageStatus(MessageStatus.PENDING)
//                .build();
//
//        PostSendMessageReq postSendMessageReq = PostSendMessageReq.builder()
//                .message(smsMessageDto)
//                .count(10000) // 1만개 테스트
////                .senderNumber("01025291674")
//                .receivers(receivers)
//                .build();
//
//        // when
//        messageService.sendMessageToBroker(postSendMessageReq, 1L);
//
//        // then
//    }
//}