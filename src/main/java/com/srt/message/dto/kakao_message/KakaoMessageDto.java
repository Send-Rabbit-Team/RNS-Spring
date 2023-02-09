package com.srt.message.dto.kakao_message;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.config.type.ButtonType;
import com.srt.message.domain.KakaoMessage;
import com.srt.message.domain.Member;
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

    public static KakaoMessage toEntity(KakaoMessageDto dto, Member member) {
        return KakaoMessage.builder()
                .member(member)
                .sender(dto.getFrom())
                .title(dto.getTitle())
                .subTitle(dto.getSubtitle())
                .content(dto.getContent())
                .image(dto.getImage())
                .description(dto.getDescription())
                .buttonTitle(dto.getButtonTitle())
                .buttonUrl(dto.getButtonUrl())
                .buttonType(dto.getButtonType())
                .build();
    }

    public static KakaoMessageDto toDto(KakaoMessage kakaoMessage) {
        return KakaoMessageDto.builder()
                .title(kakaoMessage.getTitle())
                .subtitle(kakaoMessage.getSubTitle())
                .content(kakaoMessage.getContent())
                .image(kakaoMessage.getImage())
                .description(kakaoMessage.getDescription())
                .buttonTitle(kakaoMessage.getButtonTitle())
                .buttonUrl(kakaoMessage.getButtonUrl())
                .buttonType(kakaoMessage.getButtonType())
                .build();
    }
}
