package com.srt.message.dto.message.kakao.post;

import com.srt.message.dto.message.kakao.KakaoMessageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSendKakaoMessageReq {
    private KakaoMessageDto kakaoMessageDto;

    private List<String> receivers;

    @ApiModelProperty(example = "100")
    private int count;
}
