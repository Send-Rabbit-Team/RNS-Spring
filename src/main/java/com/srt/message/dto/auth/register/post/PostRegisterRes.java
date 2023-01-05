package com.srt.message.dto.auth.register.post;

import com.srt.message.config.type.BsType;
import com.srt.message.config.type.LoginType;
import com.srt.message.domain.Member;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
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
            example = "카카오 엔터프라이즈"
    )
    private String companyName;

    @ApiModelProperty(
            example = "카카오"
    )
    private String ceoName;

    @ApiModelProperty(
            example = "12345678901"
    )
    private String bsNum;

    @ApiModelProperty(
            example = "대한민국 경기도 성남시 분당구 판교역로 235"
    )
    private String address;

    @ApiModelProperty(
            example = "IT"
    )
    private BsType bsType;

    @ApiModelProperty(
            example = "DEFAULT"
    )
    private LoginType loginType;

    public static PostRegisterRes toDto(Member member){
        return PostRegisterRes.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .companyName(member.getCompanyName())
                .ceoName(member.getCeoName())
                .bsNum(member.getBsNum())
                .address(member.getAddress())
                .bsType(member.getBsType())
                .loginType(member.getLoginType())
                .build();
    }
}
