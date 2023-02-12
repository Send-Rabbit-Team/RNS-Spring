package com.srt.message.dto.message_rule.post;

import com.srt.message.domain.MessageRule;
import com.srt.message.dto.message_rule.MessageRuleVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostSMSRuleRes {
    private List<MessageRuleVO> messageRules;

    public static PostSMSRuleRes toDto(List<MessageRule> messageRules){
        List<MessageRuleVO> messageRuleVOS = messageRules.stream()
                .map(m -> new MessageRuleVO(m.getBroker().getId(), m.getBrokerRate()))
                .collect(Collectors.toList());

        return new PostSMSRuleRes(messageRuleVOS);
    }
}
