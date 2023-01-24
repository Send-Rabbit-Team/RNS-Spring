package com.srt.message.service.dto.message.kakao;

import com.srt.message.service.dto.message.sms.SMSMessageDto;
import com.srt.message.service.dto.message_result.MessageResultDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BrokerSendKakaoMessageDto {
    private KakaoMessageDto smsMessageDto;

    private MessageResultDto messageResultDto;
}
