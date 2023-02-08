package com.srt.message.domain;

import com.srt.message.config.domain.BaseTimeEntity;
import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import lombok.*;

import javax.persistence.*;

@NamedEntityGraph(name = "Member.with.Company", attributeNodes = {
        @NamedAttributeNode(value = "company")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private String email;

    private String password;

    private String name;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(name = "profile_image_URL")
    private String profileImageURL;

    // 편의 메서드
    public void changeLoginTypeToGoogle(){
        this.loginType = LoginType.GOOGLE;
    }
}
