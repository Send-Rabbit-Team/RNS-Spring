package com.srt.message.dto.sender_number.post;

import com.srt.message.domain.SenderNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterSenderNumberRes {
    private String phoneNumber;

    private String blockNumber;

    private String memo;

    public static RegisterSenderNumberRes toDto(SenderNumber senderNumber){
        return RegisterSenderNumberRes.builder()
                .phoneNumber(senderNumber.getPhoneNumber())
                .blockNumber(senderNumber.getBlockNumber())
                .memo(senderNumber.getMemo())
                .build();
    }
}
