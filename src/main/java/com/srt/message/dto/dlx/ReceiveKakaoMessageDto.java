package com.srt.message.dto.dlx;

import com.srt.message.dto.kakao_message.KakaoMessageDto;
import com.srt.message.dto.message_result.KakaoMessageResultDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReceiveKakaoMessageDto {
    private KakaoMessageDto kakaoMessageDto;
    private KakaoMessageResultDto kakaoMessageResultDto;
}
