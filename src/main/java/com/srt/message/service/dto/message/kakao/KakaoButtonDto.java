package com.srt.message.service.dto.message.kakao;

import com.srt.message.config.type.ButtonType;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class KakaoButtonDto {
    private long kakaoButtonIds;

    private String buttonUrl;

    private String buttonTitle;

    private ButtonType buttonType;
}
