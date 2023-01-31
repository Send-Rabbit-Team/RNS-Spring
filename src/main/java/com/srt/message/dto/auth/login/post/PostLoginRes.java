package com.srt.message.service.dto.auth.login.post;

import com.srt.message.config.type.MemberType;
import com.srt.message.domain.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
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

    @ApiModelProperty(
            example = "https://abcd1234.jpg"
    )
    private String profileImageURL;

    @ApiModelProperty(
            example = "Yena Shin"
    )
    private String name;

    private MemberType memberType;

    public static PostLoginRes toDto(String jwt, Member member){
        return PostLoginRes.builder()
                .jwt(jwt)
                .memberId(member.getId())
                .profileImageURL(member.getProfileImageURL())
                .name(member.getName())
                .memberType(member.getMemberType())
                .build();
    }
}
