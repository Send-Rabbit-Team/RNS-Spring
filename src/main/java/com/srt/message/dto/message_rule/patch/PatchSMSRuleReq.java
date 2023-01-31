package com.srt.message.dto.message_rule.patch;

import com.srt.message.domain.Broker;
import com.srt.message.domain.Member;
import com.srt.message.domain.MessageRule;
import com.srt.message.dto.message_rule.MessageRuleVO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchSMSRuleReq {
    private List<MessageRuleVO> messageRules;

    public static MessageRule toEntity(Broker broker, int brokerRate, Member member){
        return MessageRule.builder()
                .member(member)
                .broker(broker)
                .brokerRate(brokerRate)
                .build();
    };
}
