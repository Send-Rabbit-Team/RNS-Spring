package com.srt.message.dto.kakaoTemplate.patch;

import com.srt.message.config.type.ButtonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PatchKakaoTemplateReq {
    private long templateId;
    private String title;
    private String subTitle;
    private String content;
    private String description;
    private String buttonUrl;
    private String buttonTitle;
    private ButtonType buttonType;
}
