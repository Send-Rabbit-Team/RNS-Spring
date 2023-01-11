package com.srt.message.dto.Contact;

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
}
