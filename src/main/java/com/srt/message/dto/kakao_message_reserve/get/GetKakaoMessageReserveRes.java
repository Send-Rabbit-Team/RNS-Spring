package com.srt.message.dto.kakao_message_reserve.get;

import com.srt.message.config.status.MessageStatus;
import com.srt.message.config.status.ReserveStatus;
import com.srt.message.config.type.ButtonType;
import com.srt.message.domain.ReserveKakaoMessage;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetKakaoMessageReserveRes {
    private long reserveId;
    private long messageId;
    private String title;
    private String subTitle;
    private String content;
    private String description;
    private String buttonUrl;
    private String buttonTitle;
    private ButtonType buttonType;
    private String createAt;
    private String cronText;
    private ReserveStatus reserveStatus;

    public static GetKakaoMessageReserveRes toDto(ReserveKakaoMessage reserveKakaoMessage) {
        return GetKakaoMessageReserveRes.builder()
                .reserveId(reserveKakaoMessage.getId())
                .messageId(reserveKakaoMessage.getKakaoMessage().getId())
                .title(reserveKakaoMessage.getKakaoMessage().getTitle())
                .subTitle(reserveKakaoMessage.getKakaoMessage().getSubTitle())
                .content(reserveKakaoMessage.getKakaoMessage().getContent())
                .description(reserveKakaoMessage.getKakaoMessage().getDescription())
                .buttonUrl(reserveKakaoMessage.getKakaoMessage().getButtonUrl())
                .buttonTitle(reserveKakaoMessage.getKakaoMessage().getButtonTitle())
                .buttonType(reserveKakaoMessage.getKakaoMessage().getButtonType())
                .createAt(reserveKakaoMessage.getCreatedAt().toString())
                .cronText(reserveKakaoMessage.getCronText())
                .reserveStatus(reserveKakaoMessage.getReserveStatus())
                .build();
    }
}
