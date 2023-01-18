package com.srt.message.service.dto.contact.patch;


import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchContactReq {

    @ApiModelProperty(
            example="1"
    )
    private long contactId;

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

    public static Contact toEntity(PatchContactReq req, ContactGroup contactGroup, Member member){
        return Contact.builder()
                .member(member)
                .contactGroup(contactGroup)
                .phoneNumber(req.getPhoneNumber())
                .memo(req.getMemo())
                .build();
    }
}
