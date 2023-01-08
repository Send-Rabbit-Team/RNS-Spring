package com.srt.message.dto.auth.register.post;

import com.srt.message.config.type.BsType;
import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import com.srt.message.domain.Company;
import com.srt.message.domain.Member;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostRegisterRes {
    @ApiModelProperty(
            example = "1"
    )
    private long memberId;

    @ApiModelProperty(
            example = "forceTlight@gmail.com"
    )
    private String email;

    @ApiModelProperty(
            example = "1q2w3e4r!"
    )
    private String password;

    @ApiModelProperty(
            example = "김형준"
    )
    private String name;

    @ApiModelProperty(
            example = "01012341234"
    )
    private String phoneNumber;

    @ApiModelProperty(
            example = "카카오 엔터프라이즈"
    )
    private String companyName;

    @ApiModelProperty(
            example = "12345678901"
    )
    private String bsNum;

    @ApiModelProperty(
            example = "COMPANY"
    )
    private MemberType memberType;

    @ApiModelProperty(
            example = "DEFAULT"
    )
    private LoginType loginType;

    public static PostRegisterRes toDto(Member member){
        Company company = member.getCompany();

        PostRegisterRes res = PostRegisterRes.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .memberType(member.getMemberType())
                .loginType(member.getLoginType())
                .build();

        if(company != null){
            res.setCompanyName(company.getCompanyName());
            res.setBsNum(company.getBsNum());
        }

        return res;
    }
}
