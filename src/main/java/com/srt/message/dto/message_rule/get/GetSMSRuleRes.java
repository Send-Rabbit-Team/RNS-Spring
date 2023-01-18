package com.srt.message.dto.message_rule.get;


import com.srt.message.domain.MessageRule;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSMSRuleRes {

    private long brokerId;
    private int brokerRate;

    public static GetSMSRuleRes toDto(MessageRule messageRule){
        return  GetSMSRuleRes.builder()
                .brokerId(messageRule.getBroker().getId())
                .brokerRate(messageRule.getBrokerRate())
                .build();
    }
}
