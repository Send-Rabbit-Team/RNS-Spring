package com.srt.message.service.dto.message_result;

import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.redis.RMessageResult;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageResultDto {
    private String rMessageResultId;

    private long messageId;

    private long brokerId;

    private long contactId;

    private MessageStatus messageStatus;

    private LocalDateTime createdAt;

    public static RMessageResult toRMessageResult(MessageResultDto dto){
        return RMessageResult.builder()
                .id(dto.getRMessageResultId())
                .messageId(dto.messageId)
                .brokerId(dto.brokerId)
                .contactId(dto.brokerId)
                .messageStatus(dto.messageStatus)
                .build();
    }
}
