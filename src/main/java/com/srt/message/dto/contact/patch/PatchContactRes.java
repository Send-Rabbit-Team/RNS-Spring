package com.srt.message.service.dto.contact.patch;


import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchContactRes {
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

    public static PatchContactRes toDto(Contact contact){
        ContactGroup contactGroup = contact.getContactGroup();

        return PatchContactRes.builder()
                .contactId(contact.getId())
                .contactGroup(contactGroup)
                .phoneNumber(contact.getPhoneNumber())
                .memo(contact.getMemo())
                .build();
    }
}
