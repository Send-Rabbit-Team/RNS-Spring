package com.srt.message.dto.block.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostBlockReq {
    @ApiModelProperty(
            example = "01012341234"
    )
    private String receiveNumber; // 수신자 번호

    @ApiModelProperty(
            example = "07012341234"
    )
    private String blockNumber;
}
