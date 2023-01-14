package com.srt.message.dto.message;

import com.srt.message.config.type.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MessageDto {
    private String from;

    private String to;

    private String subject;

    private String content;

    private String image;

    private String reserveTime;

    private MessageType messageType;

    private String scheduleCode;
}
