package com.srt.message.service.dto.contact.post;

import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostContactReq {
    @ApiModelProperty(
            example = "10"
    )
    private Long contactGroupId;

    @ApiModelProperty(
            example = "01091908201"
    )
    private String phoneNumber;

    @ApiModelProperty(
            example = "Memo Context Here"
    )
    private String memo;

    public static Contact toEntity(PostContactReq req, ContactGroup contactGroup, Member member){
        return Contact.builder()
                .member(member)
                .contactGroup(contactGroup)
                .phoneNumber(req.getPhoneNumber())
                .memo(req.getMemo())
                .build();
    }
}
