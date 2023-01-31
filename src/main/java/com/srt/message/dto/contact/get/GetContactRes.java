package com.srt.message.dto.contact.get;

import com.srt.message.domain.Contact;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetContactRes {
    private long contactId;
    private String phoneNumber;
    private String contactMemo;
    private Long groupId;
    private String groupName;

    public static GetContactRes toDto(Contact contact){
        GetContactRes getContactRes =
                GetContactRes.builder()
                        .contactId(contact.getId())
                        .phoneNumber(contact.getPhoneNumber())
                        .contactMemo(contact.getMemo())
                        .build();

        if (contact.getContactGroup() != null) {
            getContactRes.setGroupId(contact.getContactGroup().getId());
            getContactRes.setGroupName(contact.getContactGroup().getName());
        }

        return getContactRes;
    }
}
