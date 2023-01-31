package com.srt.message.dto.contact_group.get;

import com.srt.message.domain.ContactGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
