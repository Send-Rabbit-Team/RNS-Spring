package com.srt.message.dto.message_result.get;

import com.srt.message.config.status.MessageStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetMessageResultRes {
    @ApiModelProperty(
            example = "01012341234"
    )
    private String contactPhoneNumber;

    @ApiModelProperty(
            example = "KT"
    )
    private String brokerName;

    @ApiModelProperty(
            example = "SUCCESS"
    )
    private MessageStatus messageStatus;
}
