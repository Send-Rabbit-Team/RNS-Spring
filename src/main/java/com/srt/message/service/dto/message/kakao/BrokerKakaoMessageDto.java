package com.srt.message.service.dto.message.kakao;

import com.srt.message.domain.Contact;
import com.srt.message.domain.KakaoMessage;
import com.srt.message.domain.Member;
import com.srt.message.domain.Message;
import com.srt.message.service.dto.message.sms.SMSMessageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BrokerKakaoMessageDto {
    private KakaoMessageDto kakaoMessageDto;

    private KakaoMessage message;

    private List<Contact> contacts;

    private Member member;

    private int count;
}