package com.srt.message.service.dto.template.patch;

import com.srt.message.domain.Member;
import com.srt.message.domain.Template;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PatchTemplateReq {
    private long templateId;
    private String title;
    private String content;

    public static Template toEntity(PatchTemplateReq patchTemplateReq, Member member) {
        return Template.builder()
                .id(patchTemplateReq.getTemplateId())
                .member(member)
                .title(patchTemplateReq.getTitle())
                .content(patchTemplateReq.getContent())
                .build();
    }
}
