package com.srt.message.service.dto.contact_group.patch;

import com.srt.message.domain.ContactGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PatchContactGroupRes {
    @ApiModelProperty(
            example = "1"
    )
    private long ContactGroupId;

    @ApiModelProperty(
            example = "여성 회원 그룹"
    )
    private String name;

    public static PatchContactGroupRes toDto(ContactGroup contactGroup){
        return PatchContactGroupRes.builder()
                .name(contactGroup.getName())
                .build();
    }
}
