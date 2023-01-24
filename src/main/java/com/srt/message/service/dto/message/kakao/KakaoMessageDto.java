package com.srt.message.service.dto.message.kakao;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.config.type.ButtonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class KakaoMessageDto {
    private long messageId;

    private String from;

    @JsonIgnore
    private String to;

    private String subject;

    private String subtitle;

    private String content;

    private String description;

    private String image;

    private MessageStatus messageStatus;

    @JsonIgnore
    private String reserveTime;

    @JsonIgnore
    private String scheduleCode;

    // kakao button
    private String buttonUrl;

    private String buttonTitle;

    private ButtonType buttonType;
}