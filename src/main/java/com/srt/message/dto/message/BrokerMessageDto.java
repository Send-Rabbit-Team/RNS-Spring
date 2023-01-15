package com.srt.message.dto.message;

import com.srt.message.domain.Contact;
import com.srt.message.domain.Member;
import com.srt.message.domain.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BrokerMessageDto {
    private SMSMessageDto smsMessageDto;

    private Message message;

    private List<Contact> contacts;

    private Member member;

    private int count;
}