package com.srt.message.dto.message_result;

import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.redis.RKakaoMessageResult;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KakaoMessageResultDto {
    private String rMessageResultId;

    private long messageId;

    private long brokerId;

    private long contactId;

    private long retryCount;

    private MessageStatus messageStatus;

    private LocalDateTime createdAt;

    public static RKakaoMessageResult toRMessageResult(KakaoMessageResultDto dto){
        return RKakaoMessageResult.builder()
                .id(dto.getRMessageResultId())
                .kakaoMessageId(dto.getMessageId())
                .kakaoBrokerId(dto.getBrokerId())
                .contactId(dto.getContactId())
                .messageStatus(dto.getMessageStatus())
                .build();
    }
}
