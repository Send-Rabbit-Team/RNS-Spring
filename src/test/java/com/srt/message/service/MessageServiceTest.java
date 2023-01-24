package com.srt.message.service;

import com.srt.message.config.status.MessageStatus;
import com.srt.message.service.dto.message.sms.SMSMessageDto;
import com.srt.message.service.dto.message.sms.post.PostSendMessageReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MessageServiceTest {
    @Autowired
    private MessageService messageService;

    @Test
    public void sendMessageToBroker_latency_test(){
        // given
        List<String> receivers = new ArrayList<>();
        receivers.add("01093904141");

        SMSMessageDto smsMessageDto = SMSMessageDto.builder()
                .subject("[Rabbit Notification Service]")
                .content("테스트입니다")
                .from("01025291674")
                .messageStatus(MessageStatus.PENDING)
                .build();

        PostSendMessageReq postSendMessageReq = PostSendMessageReq.builder()
                .message(smsMessageDto)
                .count(10000) // 1만개 테스트
                .senderNumber("01025291674")
                .receivers(receivers)
                .build();

        // when
        messageService.sendMessageToBroker(postSendMessageReq, 1L);

        // then
    }
}