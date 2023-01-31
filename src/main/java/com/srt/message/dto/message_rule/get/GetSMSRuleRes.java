package com.srt.message.dto.message_rule.get;


import com.srt.message.domain.MessageRule;
import com.srt.message.dto.message_rule.MessageRuleVO;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSMSRuleRes {

    private List<MessageRuleVO> messageRules;

    public static GetSMSRuleRes toDto(List<MessageRule> messageRules){
        List<MessageRuleVO> getSMSRuleRes = messageRules.stream()
                .map(messageRule -> {
                    return MessageRuleVO.builder()
                            .brokerId(messageRule.getBroker().getId())
                            .brokerRate(messageRule.getBrokerRate())
                            .build();
                }).collect(Collectors.toList());

        return new GetSMSRuleRes(getSMSRuleRes);
    };
}
