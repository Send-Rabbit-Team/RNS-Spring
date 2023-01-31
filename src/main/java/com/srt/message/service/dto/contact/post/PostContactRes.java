package com.srt.message.service.dto.contact.post;

import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostContactRes {
    @ApiModelProperty(
            example = "1"
    )
    private long contactId;

    @ApiModelProperty(
            example = "10"
    )
    private long contactGroupId;

    @ApiModelProperty(
            example = "01091908201"
    )
    private String phoneNumber;

    @ApiModelProperty(
            example = "Memo Context Here"
    )
    private String memo;

    public static PostContactRes toDto(Contact contact, ContactGroup contactGroup){
        PostContactRes postContactRes = PostContactRes.builder()
                .contactId(contact.getId())
                .phoneNumber(contact.getPhoneNumber())
                .memo(contact.getMemo())
                .build();

        if (contact.getContactGroup() != null) {
            postContactRes.setContactGroupId(contact.getContactGroup().getId());
        }

        return postContactRes;
    }
}
