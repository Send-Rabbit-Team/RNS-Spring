package com.srt.message.dto.kakao_message_result.get;

import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.KakaoMessageResult;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetKakaoMessageResultRes {
    private long resultId;
    private String contactNumber;
    private String contactMemo;
    private String contactGroup;
    private String kakaoBrokerName;
    private MessageStatus messageStatus;
    private String createdAt;

    public static GetKakaoMessageResultRes toDto(KakaoMessageResult kakaoMessageResult) {
        return GetKakaoMessageResultRes.builder()
                .resultId(kakaoMessageResult.getId())
                .contactNumber(kakaoMessageResult.getContact().getPhoneNumber())
                .contactMemo(kakaoMessageResult.getContact().getMemo())
                .contactGroup(kakaoMessageResult.getContact().getContactGroup().getName())
                .kakaoBrokerName(kakaoMessageResult.getKakaoBroker().getName())
                .messageStatus(kakaoMessageResult.getMessageStatus())
                .createdAt(kakaoMessageResult.getCreatedAt().toString())
                .build();
    }
}
