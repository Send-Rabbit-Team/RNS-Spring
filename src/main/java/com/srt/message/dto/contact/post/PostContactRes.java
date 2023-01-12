package com.srt.message.dto.contact.post;

import com.srt.message.domain.Company;
import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import com.srt.message.dto.auth.register.post.PostRegisterRes;
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
    private ContactGroup contactGroup;

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
                .contactGroup(contactGroup)
                .phoneNumber(contact.getPhoneNumber())
                .memo(contact.getMemo())
                .build();
        return postContactRes;
    }
}
