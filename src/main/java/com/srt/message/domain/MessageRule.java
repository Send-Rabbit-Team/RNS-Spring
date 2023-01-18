package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import com.srt.message.config.exception.BaseException;
import com.srt.message.repository.MemberRepository;
import com.srt.message.repository.MessageRuleRepository;
import com.srt.message.service.MessageRuleService;
import com.srt.message.service.dto.message_rule.patch.PatchSMSRuleReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.srt.message.config.response.BaseResponseStatus.NOT_MATCH_MEMBER;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class MessageRule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_rule_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "broker_id")
    private Broker broker;

    private int brokerRate;

    public void editMessageRule(MessageRule messageRule) {
        this.brokerRate = messageRule.getBrokerRate();
    }
}
