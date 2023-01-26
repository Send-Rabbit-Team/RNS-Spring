package com.srt.message.service.dto.message.kakao;

import com.srt.message.config.type.ButtonType;
import com.srt.message.domain.KakaoButton;
import com.srt.message.domain.KakaoMessage;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class KakaoButtonDto {
    private long kakaoButtonId;

    private String buttonUrl;

    private String buttonTitle;

    private ButtonType buttonType;

    public static KakaoButton toEntity(KakaoButtonDto kakaoButtonDto, KakaoMessage kakaoMessage) {
        return KakaoButton.builder()
                .buttonTitle(kakaoButtonDto.getButtonTitle())
                .buttonUrl(kakaoButtonDto.getButtonUrl())
                .buttonType(kakaoButtonDto.getButtonType())
                .kakaoMessage(kakaoMessage)
                .build();
    }
}
