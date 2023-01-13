package com.srt.message.dto.contact_group;

import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactGroupDTO {
    private long id;

    private long memberId;

    private String name;

    public static ContactGroupDTO toDTO(ContactGroup contactGroup, Member member){
        return ContactGroupDTO.builder()
                .id(contactGroup.getId())
                .memberId(member.getId())
                .name(contactGroup.getName())
                .build();
    }
}
