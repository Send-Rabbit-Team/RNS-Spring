package com.srt.message.dto.kakao_message_reserve.get;

import com.srt.message.domain.ReserveMessageContact;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetKakaoMessageReserveContactRes {
    private long reserveContactId;
    private long messageId;
    private String contactGroupName;
    private String contactNumber;
    private String contactMemo;

    public static GetKakaoMessageReserveContactRes toDto(ReserveMessageContact reserveMessageContact) {
        return GetKakaoMessageReserveContactRes.builder()
                .reserveContactId(reserveMessageContact.getId())
                .messageId(reserveMessageContact.getReserveKakaoMessage().getId())
                .contactGroupName(reserveMessageContact.getContact().getContactGroup().getName())
                .contactNumber(reserveMessageContact.getContact().getPhoneNumber())
                .contactMemo(reserveMessageContact.getContact().getMemo())
                .build();
    }
}
