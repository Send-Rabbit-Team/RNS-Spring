package com.srt.message.dto.message.get;

import com.srt.message.config.status.MessageStatus;
import com.srt.message.config.type.MessageType;
import com.srt.message.domain.Message;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

import static com.srt.message.config.type.MessageType.LMS;
import static com.srt.message.config.type.MessageType.SMS;

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
    private String title;

    @ApiModelProperty(
            example = "새해 복 많이 받으세요."
    )
    private String content;

    @ApiModelProperty(
            example = "SMS"
    )
    private MessageType messageType;

    @ApiModelProperty(
            example = "ASDKJASDKJASDjk23W"
    )
    private List<String> images;

    @ApiModelProperty(
            example = "2023-02-01 14:24:25"
    )
    private String createdAt;

    public static GetMessageRes toDto(Message message){
        return GetMessageRes.builder()
                .messageId(message.getId())
                .senderNumber(message.getSenderNumber().getPhoneNumber())
                .title(message.getSubject())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .createdAt(message.getCreatedAt().toString())
                .build();
    }
}
