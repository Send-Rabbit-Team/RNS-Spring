package com.srt.message.dto.kakaoMessageRule.get;

import com.srt.message.domain.KakaoMessageRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetKakaoMessageRuleRes {
    private long kakaoMessageRuleId;
    private long kakaoBrokerId;
    private String kakaoBrokerName;
    private int kakaoBrokerRate;

    public static GetKakaoMessageRuleRes toDto(KakaoMessageRule kakaoMessageRule) {
        return GetKakaoMessageRuleRes.builder()
                .kakaoMessageRuleId(kakaoMessageRule.getId())
                .kakaoBrokerId(kakaoMessageRule.getKakaoBroker().getId())
                .kakaoBrokerName(kakaoMessageRule.getKakaoBroker().getName())
                .kakaoBrokerRate(kakaoMessageRule.getBrokerRate())
                .build();
    }
}
