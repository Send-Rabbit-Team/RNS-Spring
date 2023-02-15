package com.srt.message.dto.message_result.get;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srt.message.config.status.MessageStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetMessageResultRes {
    @ApiModelProperty(
            example = "01012341234"
    )
    private String contactPhoneNumber;

    @ApiModelProperty(
            example = "커피 그룹"
    )
    private String contactGroup;

    @ApiModelProperty(
            example = "콘트라베이스"
    )
    private String memo;

    @JsonIgnore
    @ApiModelProperty(
            example = "1"
    )
    private Long brokerId;

    @ApiModelProperty(
            example = "KT"
    )
    private String brokerName;

    @ApiModelProperty(
            example = "skt -> kt -> lg"
    )
    private String description;

    @ApiModelProperty(
            example = "SUCCESS"
    )
    private MessageStatus messageStatus;

    @ApiModelProperty(
            example = "2023-02-01 14:24:27"
    )
    private String createdAt;
}
