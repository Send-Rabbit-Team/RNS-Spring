package com.srt.message.service.dto.message_rule.post;

import com.srt.message.service.dto.message_rule.MessageRuleVO;
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
