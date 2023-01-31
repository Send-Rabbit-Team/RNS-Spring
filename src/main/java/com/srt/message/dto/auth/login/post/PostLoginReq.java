package com.srt.message.dto.auth.login.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostLoginReq {
    @ApiModelProperty(
            example = "forceTlight@gmail.com"
    )
    private String email;

    @ApiModelProperty(
            example = "1q2w3e4r!"
    )
    private String password;
}
