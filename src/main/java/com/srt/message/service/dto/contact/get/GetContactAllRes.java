package com.srt.message.service.dto.contact.get;

import com.srt.message.domain.Contact;
import com.srt.message.service.dto.contact.ContactDTO;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetContactAllRes {
    private List<ContactDTO> contacts;

    public static GetContactAllRes toDto(List<Contact> contacts){
        List<ContactDTO> contactDTOList =  contacts.stream().map(
                contact -> {
                    ContactDTO contactDTO =  ContactDTO.builder()
                            .id(contact.getId())
                            .memberId(contact.getMember().getId())
                            .phoneNumber(contact.getPhoneNumber())
                            .memo(contact.getMemo())
                            .build();

                    if(contact.getContactGroup() != null)
                        contactDTO.setGroupId(contact.getContactGroup().getId());

                    return contactDTO;
                }).collect(Collectors.toList());
        return new GetContactAllRes(contactDTOList);
    }
}
