package com.srt.message.service.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.config.type.MessageType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

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
            example = "BSAKJNDNKASDJkfetjoi312oiadsioo21basdop"
    )
    private String[] image;

    @ApiModelProperty(hidden = true)
    private MessageStatus messageStatus;

    @ApiModelProperty(
            example = "SMS"
    )
    private MessageType messageType;

    @ApiModelProperty(
            example = "추후 추가 예정"
    )
    @JsonIgnore
    private String reserveTime;

    @ApiModelProperty(
            example = "추후 추가 예정"
    )
    @JsonIgnore
    private String scheduleCode;
}
