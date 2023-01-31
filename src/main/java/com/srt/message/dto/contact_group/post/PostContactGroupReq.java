package com.srt.message.dto.contact_group.post;

import com.srt.message.domain.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostContactGroupReq {
    @ApiModelProperty(
            example = "카카오"
    )
    private String name;

}
