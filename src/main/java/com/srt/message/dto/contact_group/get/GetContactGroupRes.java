package com.srt.message.dto.contact_group.get;

import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import com.srt.message.dto.contact.ContactDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetContactGroupRes {
    private long groupId;
    private String groupName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GetContactGroupRes toDto(ContactGroup contactGroup) {
        return GetContactGroupRes.builder()
                .groupId(contactGroup.getId())
                .groupName(contactGroup.getName())
                .createdAt(contactGroup.getCreatedAt())
                .updatedAt(contactGroup.getUpdatedAt())
                .build();
    }

}
