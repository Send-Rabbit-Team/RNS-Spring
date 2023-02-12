package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import com.srt.message.config.type.ButtonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@NamedEntityGraph(name = "KakaoMessage.with.Member", attributeNodes = {
        @NamedAttributeNode(value = "member", subgraph = "member_company")},
        subgraphs = @NamedSubgraph(name = "member_company", attributeNodes = {
                @NamedAttributeNode("company")
        })
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class KakaoMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kakao_message_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String sender;

    private String title;

    private String subTitle;

    private String content;

    private String description;

    private String image;

    @Comment("알림톡 버튼 링크")
    private String buttonUrl;

    @Comment("알림톡 버튼 이름")
    private String buttonTitle;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('DS', 'WL', 'AL', 'BK', 'MD', 'AC')")
    @Comment("알림톡 버튼 종류")
    private ButtonType buttonType;

}
