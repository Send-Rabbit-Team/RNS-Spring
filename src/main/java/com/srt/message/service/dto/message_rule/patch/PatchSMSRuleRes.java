package com.srt.message.service.dto.message_rule.patch;

import com.srt.message.domain.MessageRule;
import com.srt.message.service.dto.message_rule.MessageRuleVO;
import com.srt.message.service.dto.message_rule.post.PostSMSRuleRes;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchSMSRuleRes {
    private List<MessageRuleVO> messageRules;

    public static PatchSMSRuleRes toDto(List<MessageRule> messageRules){
        List<MessageRuleVO> messageRuleVOS = messageRules.stream()
                .map(messageRule -> {
                    return MessageRuleVO.builder()
                            .brokerId(messageRule.getBroker().getId())
                            .brokerRate(messageRule.getBrokerRate())
                            .build();
                }).collect(Collectors.toList());

        return new PatchSMSRuleRes(messageRuleVOS);
    }
}
