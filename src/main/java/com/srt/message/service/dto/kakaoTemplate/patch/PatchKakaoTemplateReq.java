package com.srt.message.service.dto.kakaoTemplate.patch;

import com.srt.message.domain.KakaoTemplate;
import com.srt.message.domain.Member;
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

    public static KakaoTemplate toEntity(PatchKakaoTemplateReq patchTemplateReq, Member member) {
        return KakaoTemplate.builder()
                .id(patchTemplateReq.getTemplateId())
                .member(member)
                .title(patchTemplateReq.getTitle())
                .subTitle(patchTemplateReq.getSubTitle())
                .content(patchTemplateReq.getContent())
                .description(patchTemplateReq.getDescription())
                .build();
    }
}
