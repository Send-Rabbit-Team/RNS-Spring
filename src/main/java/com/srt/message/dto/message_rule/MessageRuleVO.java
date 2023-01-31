package com.srt.message.service.dto.message_rule;

import com.srt.message.domain.Broker;
import com.srt.message.domain.Member;
import com.srt.message.domain.MessageRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MessageRuleVO {
    @ApiModelProperty(
            example = "1"
    )
    private long brokerId;

    @ApiModelProperty(
            example = "50"
    )
    private int brokerRate;
    
    public static MessageRule toEntity(Broker broker, Member member, int brokerRate){
        return MessageRule.builder()
                .member(member)
                .broker(broker)
                .brokerRate(brokerRate)
                .build();
    }
}
