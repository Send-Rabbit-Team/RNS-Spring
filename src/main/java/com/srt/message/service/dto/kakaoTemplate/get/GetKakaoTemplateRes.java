package com.srt.message.service.dto.kakaoTemplate.get;

import com.srt.message.domain.KakaoTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetKakaoTemplateRes {
    private long templateId;
    private String title;
    private String subTitle;
    private String content;
    private String description;

    public static GetKakaoTemplateRes toDto(KakaoTemplate kakaoTemplate){
        return GetKakaoTemplateRes.builder()
                .templateId(kakaoTemplate.getId())
                .title(kakaoTemplate.getTitle())
                .subTitle(kakaoTemplate.getSubTitle())
                .content(kakaoTemplate.getContent())
                .description(kakaoTemplate.getDescription())
                .build();
    }
}
