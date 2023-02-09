package com.srt.message.dto.kakao_pay;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class KakaoPayVO {
    private String tid;
    private String orderId;
    private Long memberId;
}
