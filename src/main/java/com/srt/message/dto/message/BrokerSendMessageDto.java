package com.srt.message.dto.message;

import com.srt.message.dto.message_result.MessageResultDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BrokerSendMessageDto {
    private SMSMessageDto smsMessageDto;

    private MessageResultDto messageResultDto;
}
