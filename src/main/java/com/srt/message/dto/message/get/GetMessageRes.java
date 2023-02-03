package com.srt.message.dto.message.get;

import com.srt.message.config.type.MessageType;
import com.srt.message.domain.Message;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetMessageRes {
    @ApiModelProperty(
            example = "1"
    )
    private long messageId;

    @ApiModelProperty(
            example = "01012341234"
    )
    private String senderNumber;

    @ApiModelProperty(
            example = "새해 복 많이 받으세요."
    )
    private String content;

    @ApiModelProperty(
            example = "SMS"
    )
    private MessageType messageType;

    public static GetMessageRes toDto(Message message){
        return GetMessageRes.builder()
                .messageId(message.getId())
                .senderNumber(message.getSenderNumber().getPhoneNumber())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .build();
    }
}
