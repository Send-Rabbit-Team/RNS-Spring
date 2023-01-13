package com.srt.message.dto.broker.sms;

import com.srt.message.config.type.SMSType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SmsMessageDto {
    private SMSType smsType;

    private String from;

    private String to;

    private String subject;

    private String content;

    private List<FileDto> files;

    private String reserveTime;

    private String scheduleCode;
}
