package com.srt.message.service.dto.message.kakao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BrokerSendKakaoMessageDto {
    private KakaoMessageDto kakaoMessageDto;

    private KakaoMessageResultDto kakaoMessageResultDto;
}
