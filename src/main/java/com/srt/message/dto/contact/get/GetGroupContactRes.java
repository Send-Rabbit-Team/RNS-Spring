package com.srt.message.dto.contact.get;

import com.srt.message.domain.Contact;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetGroupContactRes {
    private long contactId;
    private String phoneNumber;
    private String contactMemo;

    public static GetGroupContactRes toDto(Contact contact){
       return  GetGroupContactRes.builder()
                        .contactId(contact.getId())
                        .phoneNumber(contact.getPhoneNumber())
                        .contactMemo(contact.getMemo())
                        .build();
    }
}
