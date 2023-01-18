package com.srt.message.service.dto.contact;

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

    public static ContactDTO toDto(Contact contact){
        ContactDTO contactDTO =
                ContactDTO.builder()
                        .id(contact.getId())
                        .memberId(contact.getMember().getId())
                        .phoneNumber(contact.getPhoneNumber())
                        .memo(contact.getMemo())
                        .build();


        if (contact.getContactGroup() != null) {
            contactDTO.setGroupId(contact.getContactGroup().getId());
        }

        return contactDTO;
    }
}
