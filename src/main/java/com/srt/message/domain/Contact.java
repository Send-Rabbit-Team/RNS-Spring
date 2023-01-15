package com.srt.message.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srt.message.config.domain.BaseEntity;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.response.BaseResponseStatus;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.dto.contact.ContactDTO;
import com.srt.message.dto.contact.patch.PatchContactReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;

@Log4j2
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Builder
public class Contact extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    @JsonIgnore // findby
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="group_id")
    @JsonIgnore
    private ContactGroup contactGroup;

    private String phoneNumber;

    private String memo;

    private void changeContactGroup(ContactGroup contactGroup){this.contactGroup = contactGroup;}
    private void changePhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}
    private void changeMemo(String memo){this.memo = memo;}

    public void quitContactGroup(long groupId) {
        if (this.contactGroup.getId() == groupId)
            this.contactGroup = null;
        else {
            throw new BaseException(BaseResponseStatus.NOT_MATCH_GROUP);
        }
    }

    public void editContact(PatchContactReq contactDto, ContactGroup contactGroup){ // dto 수정
        if(contactDto.getContactGroupId()!=null)
            this.changeContactGroup(contactGroup);

        if(contactDto.getPhoneNumber()!=null)
            this.changePhoneNumber(contactDto.getPhoneNumber());

        if(contactDto.getMemo()!=null)
            this.changeMemo(contactDto.getMemo());
    }
}
