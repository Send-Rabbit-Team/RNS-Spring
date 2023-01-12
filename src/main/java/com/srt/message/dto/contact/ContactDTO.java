package com.srt.message.dto.contact;

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
