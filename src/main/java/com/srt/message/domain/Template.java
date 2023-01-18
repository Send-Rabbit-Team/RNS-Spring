package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import com.srt.message.config.type.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Template extends BaseEntity {
    @Id
    @Comment("탬플릿 아이디")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private long id;

    @Comment("멤버 아이디")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Comment("탬플릿 제목")
    private String title;

    @Comment("탬플릿 내용")
    @Column(columnDefinition = "TEXT")
    private String content;

    @Comment("탬플릿 종류")
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('SMS', 'KAKAO')")
    private TemplateType templateType;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeTemplateType(TemplateType templateType) {
        this.templateType = templateType;
    }
}
