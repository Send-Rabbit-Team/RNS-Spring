package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
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
public class KakaoTemplate extends BaseEntity {
    @Id
    @Comment("탬플릿 아이디")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kakao_template_id")
    private long id;

    @Comment("멤버 아이디")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Comment("알림톡 제목")
    private String title;

    @Comment("알림톡 소제목")
    private String subTitle;

    @Comment("알림톡 내용")
    @Column(columnDefinition = "TEXT")
    private String content;

    @Comment("알림톡 설명")
    @Column(columnDefinition = "TEXT")
    private String description;

    public void changeTitle(String title) {
        this.title = title;
    }
    public void changeSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
    public void changeContent(String content) {
        this.content = content;
    }
    public void changeDescription(String description) {
        this.description = description;
    }

}
