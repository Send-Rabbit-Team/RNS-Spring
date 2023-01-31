package com.srt.message.service.dto.template.post;

import com.srt.message.domain.Member;
import com.srt.message.domain.Template;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostTemplateReq {
    private String title;
    private String content;

    public static Template toEntity(PostTemplateReq postTemplateReq, Member member){
        return Template.builder()
                .member(member)
                .title(postTemplateReq.getTitle())
                .content(postTemplateReq.getContent())
                .build();
    }

}
