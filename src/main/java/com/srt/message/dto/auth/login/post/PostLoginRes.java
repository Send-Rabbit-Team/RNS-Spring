package com.srt.message.dto.auth.login.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostLoginRes {
    @ApiModelProperty(
            example = "123asdaf12gGdddsadf21afa32hre23h"
    )
    private String jwt;

    @ApiModelProperty(
            example = "1"
    )
    private Long memberId;

}
