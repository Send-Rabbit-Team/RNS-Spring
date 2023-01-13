package com.srt.message.dto.contact;

import com.srt.message.domain.Contact;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ContactDTO {
    long id;

    long memberId;

    long groupId;

    String phoneNumber;

    String memo;

    public static ContactDTO toDto(Contact contact){
        return ContactDTO.builder()
                    .id(contact.getId())
                    .memberId(contact.getMember().getId())
                    .groupId(contact.getContactGroup().getId())
                    .phoneNumber(contact.getPhoneNumber())
                    .memo(contact.getMemo())
                    .build();
    }
}
