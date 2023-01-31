package com.srt.message.dto.kakaoTemplate.post;

import com.srt.message.config.type.ButtonType;
import com.srt.message.domain.KakaoTemplate;
import com.srt.message.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostKakaoTemplateReq {
    private String title;
    private String subTitle;
    private String content;
    private String description;
    private String buttonUrl;
    private String buttonTitle;
    private ButtonType buttonType;

    public static KakaoTemplate toEntity(PostKakaoTemplateReq postTemplateReq, Member member){
        return KakaoTemplate.builder()
                .member(member)
                .title(postTemplateReq.getTitle())
                .subTitle(postTemplateReq.getSubTitle())
                .content(postTemplateReq.getContent())
                .description(postTemplateReq.getDescription())
                .buttonUrl(postTemplateReq.getButtonUrl())
                .buttonTitle(postTemplateReq.getButtonTitle())
                .buttonType(postTemplateReq.getButtonType())
                .build();
    }

}
