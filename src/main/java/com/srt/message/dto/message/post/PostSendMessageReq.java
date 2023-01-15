package com.srt.message.dto.message.post;

import com.srt.message.dto.message.SMSMessageDto;
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

    private String senderNumber;

    private List<String> receivers;

    @ApiModelProperty(example = "100")
    private int count;
}
