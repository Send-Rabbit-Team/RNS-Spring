package com.srt.message.dto.auth.register.google;

import com.srt.message.config.type.BsType;
import com.srt.message.config.type.LoginType;
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
    private String email;

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

    public static Member toEntity(GoogleRegisterReq req){
        return Member.builder()
                .address(req.getAddress())
                .bsNum(req.getBsNum())
                .ceoName(req.getCeoName())
                .companyName(req.getCompanyName())
                .email(req.getEmail())
                .bsType(req.getBsType())
                .loginType(req.getLoginType())
                .build();
    }
}
