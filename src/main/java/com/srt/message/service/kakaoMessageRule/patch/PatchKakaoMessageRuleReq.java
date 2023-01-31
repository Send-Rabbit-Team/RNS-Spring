package com.srt.message.service.kakaoMessageRule.patch;

import com.srt.message.domain.KakaoBroker;
import com.srt.message.domain.KakaoMessageRule;
import com.srt.message.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PatchKakaoMessageRuleReq {
    private long kakaoMessageRuleId;
    private long kakaoBrokerId;
    private String kakaoBrokerName;
    private int kakaoBrokerRate;

    public static KakaoMessageRule toEntity(PatchKakaoMessageRuleReq patchKakaoMessageRuleReq, Member member) {
        KakaoBroker kakaoBroker = KakaoBroker.builder()
                .id(patchKakaoMessageRuleReq.getKakaoMessageRuleId())
                .name(patchKakaoMessageRuleReq.getKakaoBrokerName())
                .build();

        KakaoMessageRule kakaoMessageRule = KakaoMessageRule.builder()
                .id(patchKakaoMessageRuleReq.getKakaoBrokerId())
                .member(member)
                .kakaoBroker(kakaoBroker)
                .brokerRate(patchKakaoMessageRuleReq.getKakaoBrokerRate())
                .build();

        return kakaoMessageRule;
    }
}
