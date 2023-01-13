package com.srt.message.dto.contact_group.post;

import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostContactGroupRes {
    @ApiModelProperty(
            example = "1"
    )
    private long contactGroupId;

    @ApiModelProperty(
            example = "남성 회원 그룹"
    )
    private String name;

    @ApiModelProperty(
            example = "오영주"
    )
    private Member member;

    public static PostContactGroupRes toDto(ContactGroup contactGroup){
        return PostContactGroupRes.builder()
                .contactGroupId(contactGroup.getId())
                .name(contactGroup.getName())
                .member(contactGroup.getMember())
                .build();
    };
}


