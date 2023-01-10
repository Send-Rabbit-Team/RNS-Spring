package com.srt.message.service;

import com.srt.message.config.auditor.LoginMember;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.AuthPhoneNumberStatus;
import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import com.srt.message.domain.Company;
import com.srt.message.domain.Member;
import com.srt.message.domain.redis.AuthPhoneNumber;
import com.srt.message.dto.auth.login.post.PostLoginReq;
import com.srt.message.dto.auth.login.post.PostLoginRes;
import com.srt.message.dto.auth.register.google.GoogleRegisterReq;
import com.srt.message.dto.auth.register.google.GoogleRegisterRes;
import com.srt.message.dto.auth.register.post.PostRegisterReq;
import com.srt.message.dto.auth.register.post.PostRegisterRes;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.jwt.JwtService;
import com.srt.message.repository.CompanyRepository;
import com.srt.message.repository.MemberRepository;
import com.srt.message.repository.redis.AuthPhoneNumberRedisRepository;
import com.srt.message.utils.encrypt.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.srt.message.config.response.BaseResponseStatus.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final MemberRepository memberRepository;

    private final CompanyRepository companyRepository;

    private final AuthPhoneNumberRedisRepository authPhoneNumberRedisRepository;

    private final LoginMember loginMember;

    // 회원가입
    @Transactional(readOnly = false)
    public PostRegisterRes defaultSignUp(PostRegisterReq postRegisterReq){
        // 중복된 회원 확인
        if(memberRepository.findByEmailIgnoreCase(postRegisterReq.getEmail()).isPresent())
            throw new BaseException(ALREADY_EXIST_EMAIL);

        // 비밀번호, 비밀번호 같은지 확인
        if(!postRegisterReq.getPassword().equals(postRegisterReq.getCheckPassword()))
            throw new BaseException(NOT_MATCH_CHECK_PASSWORD);

        // 비밀번호 암호화
        String password = postRegisterReq.getPassword();
        password = SHA256.encrypt(password);
        postRegisterReq.setPassword(password);

        Company company = null;

        if(postRegisterReq.getMemberType() == MemberType.COMPANY){
            company = PostRegisterReq.toCompanyEntity(postRegisterReq);
            companyRepository.save(company);
        }

        // 인증 번호 검증
        AuthPhoneNumber authPhoneNumber =
                authPhoneNumberRedisRepository.findById(postRegisterReq.getPhoneNumber())
                        .orElseThrow(() -> new BaseException(NOT_AUTH_PHONE_NUMBER));

        if(authPhoneNumber.getAuthPhoneNumberStatus() != AuthPhoneNumberStatus.CONFIRM)
            throw new BaseException(NOT_AUTH_PHONE_NUMBER);

        Member member = PostRegisterReq.toMemberEntity(postRegisterReq, company);
        memberRepository.save(member);

        authPhoneNumberRedisRepository.delete(authPhoneNumber);

        return PostRegisterRes.toDto(member);
    }

    // 구글 회원가입
    @Transactional(readOnly = false)
    public GoogleRegisterRes googleSignUp(GoogleRegisterReq googleRegisterReq) {
        if ((memberRepository.findByEmailIgnoreCase(googleRegisterReq.getEmail())).isPresent())
            throw new BaseException(ALREADY_EXIST_EMAIL);

        Company company = null;

        if(googleRegisterReq.getMemberType() == MemberType.COMPANY){
            company = GoogleRegisterReq.toCompanyEntity(googleRegisterReq);
            companyRepository.save(company);
        }

        Member member = GoogleRegisterReq.toMemberEntity(googleRegisterReq, company);
        member.changeLoginTypeToGoogle();
        memberRepository.save(member);

        return GoogleRegisterRes.toDto(member);
    }

    // 로그인
    public PostLoginRes defaultSignIn(PostLoginReq postLoginReq){
        String email = postLoginReq.getEmail();
        String password = postLoginReq.getPassword();

        // 이메일 일치 X
        Member member = memberRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BaseException(NOT_EXIST_EMAIL));

        // 비밀번호 일치 X
        if(!member.getPassword().equals(SHA256.encrypt(password))){
            throw new BaseException(NOT_MATCH_PASSWORD);
        }

        // jwt
        JwtInfo jwtInfo = new JwtInfo(member.getId());
        JwtService jwtService = new JwtService();
        String jwt = jwtService.createJwt(jwtInfo);

        return new PostLoginRes(jwt, member.getId());
    }

    // 구글 로그인
    public PostLoginRes googleSignIn(String email){
        // 이메일 일치 X
        Member member = memberRepository.findByEmailIgnoreCaseAndLoginType(email, LoginType.GOOGLE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_EMAIL));

        // jwt
        JwtInfo jwtInfo = new JwtInfo(member.getId());
        JwtService jwtService = new JwtService();
        String jwt = jwtService.createJwt(jwtInfo);

        return new PostLoginRes(jwt, member.getId());
    }

    public boolean checkExistGoogleEmail(String email){
        if(memberRepository.findByEmailIgnoreCaseAndLoginType(email, LoginType.GOOGLE).isPresent())
            return true;

        return false;
    }

    // Audit
    public void updateLoginMemberById(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));

        loginMember.updateLoginMember(member);
    }
}
