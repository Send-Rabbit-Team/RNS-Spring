package com.srt.message.dto.member.get;

import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import com.srt.message.domain.Company;
import com.srt.message.domain.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetInfoMemberRes {
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
            example = "https://objectstorage.kr-central-1.kakaoi.io/v1/586d691a32c5421b859e89fd7a7f8dcd/message/img%2Fprofile%2FprofileImg.png"
    )
    private String profileImgUrl;

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

    private String kakaoBizId;

    public static GetInfoMemberRes toDto(Member member){
        Company company = member.getCompany();

        GetInfoMemberRes getInfoMemberRes = GetInfoMemberRes.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .profileImgUrl(member.getProfileImageURL())
                .memberType(member.getMemberType())
                .loginType(member.getLoginType())
                .build();

        if(company != null){
            getInfoMemberRes.setCompanyName(company.getCompanyName());
            getInfoMemberRes.setBsNum(company.getBsNum());
            getInfoMemberRes.setKakaoBizId(company.getKakaoBizId());
        }


        return getInfoMemberRes;
    }
}
