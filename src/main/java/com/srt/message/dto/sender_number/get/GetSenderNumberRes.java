package com.srt.message.dto.sender_number.get;

import com.srt.message.domain.SenderNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetSenderNumberRes {
    private long id;
    private String memo;
    private String phoneNumber;
    private String blockNumber;

    public static GetSenderNumberRes toDto(SenderNumber senderNumber){
        return GetSenderNumberRes.builder()
                .id(senderNumber.getId())
                .memo(senderNumber.getMemo())
                .phoneNumber(senderNumber.getPhoneNumber())
                .blockNumber(senderNumber.getBlockNumber())
                .build();
    }
}
