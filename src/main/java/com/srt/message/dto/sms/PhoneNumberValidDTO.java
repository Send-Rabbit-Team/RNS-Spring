package com.srt.message.service.dto.sms;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class PhoneNumberValidDTO {
    @ApiModelProperty(
            example = "01012341234"
    )
    String phoneNumber;

    @ApiModelProperty(
            example = "123123"
    )
    String authToken;
}
