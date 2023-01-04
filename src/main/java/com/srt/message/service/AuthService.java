package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.Member;
import com.srt.message.dto.auth.login.post.PostLoginReq;
import com.srt.message.dto.auth.login.post.PostLoginRes;
import com.srt.message.dto.auth.register.google.GoogleRegisterReq;
import com.srt.message.dto.auth.register.google.GoogleRegisterRes;
import com.srt.message.dto.auth.register.post.PostRegisterReq;
import com.srt.message.dto.auth.register.post.PostRegisterRes;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.jwt.JwtService;
import com.srt.message.repository.MemberRepository;
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

    // 회원가입
    @Transactional(readOnly = false)
    public PostRegisterRes defaultSignUp(PostRegisterReq postRegisterReq){
        // 중복된 회원 확인
        if(memberRepository.findMemberByEmail(postRegisterReq.getEmail()).isPresent())
            throw new BaseException(ALREADY_EXIST_EMAIL);

        // 비밀번호, 비밀번호 같은지 확인
        if(!postRegisterReq.getPassword().equals(postRegisterReq.getCheckPassword()))
            throw new BaseException(NOT_MATCH_CHECK_PASSWORD);

        // 비밀번호 암호화
        String password = postRegisterReq.getPassword();
        password = SHA256.encrypt(password);
        postRegisterReq.setPassword(password);

        Member member = PostRegisterReq.toEntity(postRegisterReq);
        memberRepository.save(member);

        return PostRegisterRes.toDto(member);
    }

    // 구글 회원가입
    public GoogleRegisterRes googleSignUp(GoogleRegisterReq googleRegisterReq) {
        if ((memberRepository.findMemberByEmail(googleRegisterReq.getEmail())).isPresent())
            throw new BaseException(ALREADY_EXIST_EMAIL);

        Member member = GoogleRegisterReq.toEntity(googleRegisterReq);
        member.changeLoginTypeToGoogle();
        memberRepository.save(member);

        return GoogleRegisterRes.toDto(member);
    }

    // 로그인
    public PostLoginRes defaultSignIn(PostLoginReq postLoginReq){
        String email = postLoginReq.getEmail();
        String password = postLoginReq.getPassword();

        // 이메일 일치 X
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new BaseException(NOT_EXIST_EMAIL));

        // 비밀번호 일치 X
        if(!member.getPassword().equals(SHA256.encrypt(password))){
            throw new BaseException(NOT_MATCH_PASSWORD);
        }

        JwtInfo jwtInfo = new JwtInfo(member.getId());

        JwtService jwtService = new JwtService();
        String jwt = jwtService.createJwt(jwtInfo);

        return new PostLoginRes(jwt, member.getId());
    }
}
