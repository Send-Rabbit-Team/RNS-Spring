package com.srt.message.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.config.type.MessageType;
import com.srt.message.domain.Message;
import com.srt.message.domain.ReserveMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SMSMessageDto {
    @ApiModelProperty(
            example = "01025291674"
    )
    private String from;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String to;

    @ApiModelProperty(
            example = "[카카오 엔터프라이즈]"
    )
    private String subject;

    @ApiModelProperty(
            example = "2023년 사업 계획서입니다."
    )
    private String content;

    @ApiModelProperty(
            example = "[BSAKJNDNKASDJkfetjoi312oiadsioo21basdop, asdjknasdnasdnjsadkj1241]"
    )
    private String[] images;

    @ApiModelProperty(hidden = true)
    private MessageStatus messageStatus;

    @ApiModelProperty(
            example = "SMS"
    )
    private MessageType messageType;

    /**
     * 예약 발송
     */
    @ApiModelProperty(
            example = "0/1 * * * * ?"
    )
    private String cronExpression;

    @ApiModelProperty(
            example = "매일 오전 3시"
    )
    private String cronText;

    public static SMSMessageDto toDto(Message message, ReserveMessage reserveMessage){
        return SMSMessageDto.builder()
                .subject(message.getSubject())
                .content(message.getContent())
                .from(message.getSenderNumber().getPhoneNumber())
                .messageType(message.getMessageType())
                .messageStatus(MessageStatus.PENDING)
                .cronExpression(reserveMessage.getCronExpression())
                .cronText(reserveMessage.getCronText())
                .build();
    }
}
