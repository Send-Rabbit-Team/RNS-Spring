package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import com.srt.message.dto.contact.patch.PatchContactReq;
import com.srt.message.dto.contact_group.patch.PatchContactGroupReq;
import com.srt.message.dto.contact_group.post.PostContactGroupReq;
import lombok.*;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Builder
public class ContactGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="group_id")
    private long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    private String name;

    private void changeName(String name){this.name = name;}

    public static ContactGroup toEntity(PostContactGroupReq contactGroupDTO, Member member){
        return ContactGroup.builder()
                .member(member)
                .name(contactGroupDTO.getName())
                .build();
    }

    public ContactGroup editContactGroup(PatchContactGroupReq contactGroupDto){ // dto 수정
        if(contactGroupDto.getName()!=null)
            this.changeName(contactGroupDto.getName());

        return this;
    }
}
