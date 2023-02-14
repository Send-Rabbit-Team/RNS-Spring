package com.srt.message.dto.block.post;

import com.srt.message.domain.Block;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostBlockRes {
    @ApiModelProperty(example = "01012341234")
    private String senderNumber;

    @ApiModelProperty(example = "01012345678")
    private String receiveNumber;

    public static PostBlockRes toDto(Block block){
        return PostBlockRes.builder()
                .senderNumber(block.getSenderNumber())
                .receiveNumber(block.getReceiveNumber())
                .build();
    }
}
