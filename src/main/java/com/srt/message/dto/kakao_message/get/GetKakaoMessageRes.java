package com.srt.message.dto.kakao_message.get;

import com.srt.message.config.type.ButtonType;
import com.srt.message.domain.KakaoMessage;
import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetKakaoMessageRes {
    private long messageId;
    private String sender;
    private String title;
    private String subTitle;
    private String content;
    private String description;
    private String buttonUrl;
    private String buttonTitle;
    private ButtonType buttonType;
    private String createAt;

    public static GetKakaoMessageRes toDto(KakaoMessage kakaoMessage) {
        return GetKakaoMessageRes.builder()
                .messageId(kakaoMessage.getId())
                .sender(kakaoMessage.getSender())
                .title(kakaoMessage.getTitle())
                .subTitle(kakaoMessage.getSubTitle())
                .content(kakaoMessage.getContent())
                .description(kakaoMessage.getDescription())
                .buttonUrl(kakaoMessage.getButtonUrl())
                .buttonTitle(kakaoMessage.getButtonTitle())
                .buttonType(kakaoMessage.getButtonType())
                .createAt(kakaoMessage.getCreatedAt().toString())
                .build();
    }
}
