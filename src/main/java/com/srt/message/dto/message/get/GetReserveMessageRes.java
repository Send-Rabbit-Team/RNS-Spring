package com.srt.message.dto.message.get;

import com.srt.message.config.status.ReserveStatus;
import com.srt.message.config.type.MessageType;
import com.srt.message.domain.Message;
import com.srt.message.domain.ReserveMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetReserveMessageRes {
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

    @ApiModelProperty(
            example = "2023-02-01 14:24:25"
    )
    private String createdAt;

    @ApiModelProperty(
            example = "매일 오후 1시"
    )
    private String cronText;

    @ApiModelProperty(
            example = "1"
    )
    private int sendCount;

    @ApiModelProperty(
            example = "PROCESSING"
    )
    private ReserveStatus reserveStatus;

    public static GetReserveMessageRes toDto(ReserveMessage reserveMessage, String sendCount){
        Message message = reserveMessage.getMessage();

        String content = (message.getSubject() == null)? message.getContent():
                message.getSubject() + "\n" + message.getContent();

        return GetReserveMessageRes.builder()
                .messageId(message.getId())
                .senderNumber(message.getSenderNumber().getPhoneNumber())
                .content(content)
                .messageType(message.getMessageType())
                .createdAt(message.getCreatedAt().toString())
                .cronText(reserveMessage.getCronText())
                .sendCount(Integer.parseInt(sendCount))
                .reserveStatus(reserveMessage.getReserveStatus())
                .build();
    }
}
