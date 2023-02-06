package com.srt.message.dto.kakao_message;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.config.type.ButtonType;
import lombok.*;

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

    private String buttonTitle;

    private String buttonUrl;

    private ButtonType buttonType;

    private String cronExpression;

    private String cronText;
}
