package com.srt.message.dto.auth.register.post;

import com.srt.message.config.type.BsType;
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
public class PostRegisterReq {
    @ApiModelProperty(
            example = "forceTlight@gmail.com"
    )
    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$",
            message = "이메일 양식에 맞게 입력해 주시기 바랍니다.")
    private String email;

    @ApiModelProperty(
            example = "1q2w3e4r!"
    )
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,15}$",
            message = "비밀번호는 최소 8 자로 문자, 숫자 및 특수 문자를 최소 하나씩 포함해서 8-15자리 이내로 입력해주세요.")
    private String password;

    @ApiModelProperty(
            example = "1q2w3e4r!"
    )
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,15}$",
            message = "비밀번호는 최소 8 자로 문자, 숫자 및 특수 문자를 최소 하나씩 포함해서 8-15자리 이내로 입력해주세요.")
    private String checkPassword;

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

    public static Member toMemberEntity(PostRegisterReq req, Company company){
        return Member.builder()
                .email(req.getEmail())
                .password(req.getPassword())
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .company(company)
                .memberType(req.getMemberType())
                .loginType(req.getLoginType())
                .build();
    }

    public static Company toCompanyEntity(PostRegisterReq req){
        return Company.builder()
                .companyName(req.getCompanyName())
                .bsNum(req.getBsNum())
                .build();
    }
}
