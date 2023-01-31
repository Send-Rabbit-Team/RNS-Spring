package com.srt.message.service.dto.contact_group.patch;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.Example;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchContactGroupReq {

    @ApiModelProperty(
            example = "1"
    )
    private long contactGroupId;

    @ApiModelProperty(
            example = "오영주"
    )
    private String name;

}
