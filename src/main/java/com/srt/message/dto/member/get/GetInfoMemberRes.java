package com.srt.message.dto.member.get;

import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import com.srt.message.domain.Company;
import com.srt.message.domain.Member;
import com.srt.message.dto.company.CompanyDto;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetInfoMemberRes {
    private CompanyDto company;

    private String email;

    private String password;

    private String name;

    private String phoneNumber;

    private MemberType memberType;

    private LoginType loginType;

    public static GetInfoMemberRes toDto(Member member){
        Company company = member.getCompany();

        GetInfoMemberRes getInfoMemberRes = GetInfoMemberRes.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .memberType(member.getMemberType())
                .loginType(member.getLoginType())
                .build();

        getInfoMemberRes.setCompany(CompanyDto.toDto(company));

        return getInfoMemberRes;
    }
}
