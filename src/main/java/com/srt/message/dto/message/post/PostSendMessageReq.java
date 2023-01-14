package com.srt.message.dto.message.post;

import com.srt.message.config.domain.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostSendMessageReq {
    private String from;

    private String to;

    private String subject;

    private String content;

    private String image;

    private String reserveTime;

    private String scheduleCode;

    private MessageType messageType;
}
