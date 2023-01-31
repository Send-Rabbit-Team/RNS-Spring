package com.srt.message.service.kakaoMessageRule.post;

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
public class PostKakaoMessageRuleReq {
    private long kakaoBrokerId;
    private String kakaoBrokerName;
    private int kakaoBrokerRate;

    public static KakaoMessageRule toEntity(PostKakaoMessageRuleReq postKakaoMessageRuleReq, Member member) {
        KakaoBroker kakaoBroker = KakaoBroker.builder()
                .id(postKakaoMessageRuleReq.getKakaoBrokerId())
                .name(postKakaoMessageRuleReq.getKakaoBrokerName())
                .build();

        KakaoMessageRule kakaoMessageRule = KakaoMessageRule.builder()
                .member(member)
                .kakaoBroker(kakaoBroker)
                .brokerRate(postKakaoMessageRuleReq.getKakaoBrokerRate())
                .build();

        return kakaoMessageRule;
    }
}
