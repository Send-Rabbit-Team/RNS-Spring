package com.srt.message.service.dto.message.kakao.post;

import com.srt.message.service.dto.message.kakao.KakaoMessageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSendKakaoMessageReq {
    private KakaoMessageDto message;

    private String senderNumber;

    private List<String> receivers;

    @ApiModelProperty(example = "100")
    private int count;
}
