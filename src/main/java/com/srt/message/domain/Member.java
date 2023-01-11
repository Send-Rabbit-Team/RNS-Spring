package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import com.srt.message.config.domain.BaseTimeEntity;
import com.srt.message.config.type.BsType;
import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import lombok.*;

import javax.persistence.*;

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

    @OneToOne
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
    private String profileImageURL = getDefaultProfileImg();

    // 편의 메서드
    public void changeLoginTypeToGoogle(){
        this.loginType = LoginType.GOOGLE;
    }

    public String getDefaultProfileImg(){
        return "[김형준] [오후 2:02] https://objectstorage.kr-central-1.kakaoi.io/v1/586d691a32c5421b859e89fd7a7f8dcd/message/img%2Fprofile%2FprofileImg.png";
    }
}
