package com.srt.message.config.auditor;

import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import com.srt.message.domain.Company;
import com.srt.message.domain.Member;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
public class LoginMember {
    private long id;

    private Company company;

    private String email;

    private String password;

    private String name;

    private String phoneNumber;

    private MemberType memberType;

    private LoginType loginType;

    public void updateLoginMember(Member member){
        this.id = member.getId();
        this.company = member.getCompany();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.name = member.getName();
        this.phoneNumber = member.getPhoneNumber();
        this.memberType = member.getMemberType();
        this.loginType = member.getLoginType();
    }

    public Member toEntity(){
        return Member.builder()
                .id(id)
                .company(company)
                .email(email)
                .password(password)
                .name(name)
                .phoneNumber(phoneNumber)
                .memberType(memberType)
                .loginType(loginType)
                .build();
    }
}
