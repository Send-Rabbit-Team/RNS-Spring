package com.srt.message.dto.sms;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MessageDTO {
    @ApiModelProperty(
            example = "01012341234"
    )
    String to;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String content;
}
