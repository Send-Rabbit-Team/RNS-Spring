package com.srt.message.dto.auth.register.google;

import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import com.srt.message.domain.Company;
import com.srt.message.domain.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GoogleRegisterReq {
    @ApiModelProperty(
            example = "forceTlight@gmail.com"
    )
    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$",
            message = "이메일 양식에 맞게 입력해 주시기 바랍니다.")
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
            example = "12345678901"
    )
    private String bsNum;

    @ApiModelProperty(
            example = "PERSON"
    )
    private MemberType memberType;

    @ApiModelProperty(
            example = "GOOGLE"
    )
    private LoginType loginType;

    private String kakaoBizId;

    public static Member toMemberEntity(GoogleRegisterReq req, Company company){
        return Member.builder()
                .email(req.getEmail())
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .company(company)
                .memberType(req.getMemberType())
                .loginType(req.getLoginType())
                .build();
    }

    public static Company toCompanyEntity(GoogleRegisterReq req){
        return Company.builder()
                .companyName(req.getCompanyName())
                .bsNum(req.getBsNum())
                .kakaoBizId(req.getKakaoBizId())
                .build();
    }
}
