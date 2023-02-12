package com.srt.message.dto.template.get;

import com.srt.message.domain.Template;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTemplateRes {
    private long templateId;
    private String title;
    private String content;

    public static GetTemplateRes toDto(Template template){
        return GetTemplateRes.builder()
                .templateId(template.getId())
                .title(template.getTitle())
                .content(template.getContent())
                .build();
    }
}
