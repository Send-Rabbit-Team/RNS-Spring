package com.srt.message.service.dto.message.kakao;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srt.message.config.status.MessageStatus;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KakaoMessageDto {
    private long messageId;

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

    private List<KakaoButtonDto> kakaoButtonDtoList;
}
