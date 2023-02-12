package com.srt.message.dto.contact;

import com.srt.message.domain.Contact;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ContactDTO {
    private long id;

    private long memberId;

    private long groupId;

    private String phoneNumber;

    private String memo;

    private String groupName;

    public static ContactDTO toDto(Contact contact){
        ContactDTO contactDTO =
                ContactDTO.builder()
                        .id(contact.getId())
                        .memberId(contact.getMember().getId())
                        .phoneNumber(contact.getPhoneNumber())
                        .memo(contact.getMemo())
                        .groupName(contact.getContactGroup().getName())
                        .build();

        if (contact.getContactGroup() != null) {
            contactDTO.setGroupId(contact.getContactGroup().getId());
        }

        return contactDTO;
    }
}
