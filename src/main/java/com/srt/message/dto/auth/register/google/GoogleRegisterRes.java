package com.srt.message.dto.auth.register.google;

import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import com.srt.message.domain.Company;
import com.srt.message.domain.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GoogleRegisterRes {
    @ApiModelProperty(
            example = "1"
    )
    private long memberId;

    @ApiModelProperty(
            example = "forceTlight@gmail.com"
    )
    private String email;

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
            example = "https://objectstorage.kr-central-1.kakaoi.io/v1/586d691a32c5421b859e89fd7a7f8dcd/message/img%2Fprofile%2FprofileImg.png"
    )
    private String profileImgUrl;

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

    public static GoogleRegisterRes toDto(Member member){
        Company company = member.getCompany();

        GoogleRegisterRes res = GoogleRegisterRes.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .profileImgUrl(member.getProfileImageURL())
                .loginType(member.getLoginType())
                .build();

        if(company != null){
            res.setCompanyName(company.getCompanyName());
            res.setBsNum(company.getBsNum());
        }

        return res;
    }
}
