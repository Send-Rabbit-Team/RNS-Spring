package com.srt.message.dto.message_rule.post;

import com.srt.message.dto.message_rule.MessageRuleVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostSMSRuleReq {
    private List<MessageRuleVO> messageRules;
}
