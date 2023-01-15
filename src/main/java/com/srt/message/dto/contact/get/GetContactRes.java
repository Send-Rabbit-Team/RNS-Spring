package com.srt.message.dto.contact.get;

import com.srt.message.domain.Contact;
import com.srt.message.dto.contact.ContactDTO;
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
    private long groupId;
    private String groupName;

    public static GetContactRes toDto(Contact contact){
        GetContactRes getContactRes =
                GetContactRes.builder()
                        .contactId(contact.getId())
                        .phoneNumber(contact.getPhoneNumber())
                        .contactMemo(contact.getMemo())
                        .groupId(contact.getContactGroup().getId())
                        .groupName(contact.getContactGroup().getName())
                        .build();

        return getContactRes;
    }
}
