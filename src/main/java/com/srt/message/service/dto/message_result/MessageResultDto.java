package com.srt.message.service.dto.message_result;

import com.srt.message.config.status.MessageStatus;
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
}
