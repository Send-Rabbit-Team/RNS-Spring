package com.srt.message.dto.message_rule.get;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSMSRuleReq {
    private long memberId;
}
