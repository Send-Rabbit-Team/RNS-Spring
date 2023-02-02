package com.srt.message.dto.message.post;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostReserveMessageReq {
    @ApiModelProperty(
            example = "1"
    )
    private long messageId;

    @ApiModelProperty(
            example = "0/1 * * * * ?"
    )
    private String cronExpression;

    @ApiModelProperty(
            example = "['01012341234','01012531253']"
    )
    private List<String> receivers;
}
