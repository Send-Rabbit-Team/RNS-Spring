package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class KakaoMessageRule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kakao_message_rule_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kakao_broker_id")
    private KakaoBroker kakaoBroker;

    private int brokerRate;

    public void editMessageRule(KakaoMessageRule messageRule) {
        this.brokerRate = messageRule.getBrokerRate();
    }
}
