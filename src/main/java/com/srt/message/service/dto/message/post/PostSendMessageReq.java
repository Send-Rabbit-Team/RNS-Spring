package com.srt.message.service.dto.message.post;

import com.srt.message.service.dto.message.SMSMessageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostSendMessageReq {
    private SMSMessageDto message;

    private List<String> receivers;

    @ApiModelProperty(example = "100")
    private int count;
}
