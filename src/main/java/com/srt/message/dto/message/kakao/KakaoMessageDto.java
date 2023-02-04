package com.srt.message.dto.message.kakao;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.config.type.ButtonType;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KakaoMessageDto {
    private String from;

    @JsonIgnore
    private String to;

    private String title;

    private String subtitle;

    private String content;

    private String description;

    private String image;

    private MessageStatus messageStatus;

    @JsonIgnore
    private String reserveTime;

    @JsonIgnore
    private String scheduleCode;

    private String buttonTitle;

    private String buttonUrl;

    private ButtonType buttonType;
}
