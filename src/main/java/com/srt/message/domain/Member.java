package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import com.srt.message.config.type.BsType;
import com.srt.message.config.type.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private long id;

    private String email;

    private String password;

    private String companyName;

    private String ceoName;

    private String bsNum;

    private String address;

    @Enumerated(EnumType.STRING)
    private BsType bsType;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;
}
