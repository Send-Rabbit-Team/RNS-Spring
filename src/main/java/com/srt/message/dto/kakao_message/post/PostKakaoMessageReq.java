package com.srt.message.dto.kakao_message.post;

import com.srt.message.dto.kakao_message.KakaoMessageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostKakaoMessageReq {
    private KakaoMessageDto kakaoMessageDto;

    private List<String> receivers;

    @ApiModelProperty(example = "100")
    private int count;
}
