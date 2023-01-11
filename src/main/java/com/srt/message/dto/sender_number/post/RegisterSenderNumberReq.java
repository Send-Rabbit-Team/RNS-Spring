package com.srt.message.dto.sender_number.post;

import com.srt.message.domain.SenderNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterSenderNumberReq {
    private String phoneNumber;

    private String memo;

    public static SenderNumber toEntity(RegisterSenderNumberReq registerSenderNumberReq){
        return SenderNumber.builder()
                .phoneNumber(registerSenderNumberReq.getPhoneNumber())
                .memo(registerSenderNumberReq.getMemo())
                .build();
    }
}
