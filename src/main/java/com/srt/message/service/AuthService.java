package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.Member;
import com.srt.message.dto.auth.register.post.PostRegisterReq;
import com.srt.message.dto.auth.register.post.PostRegisterRes;
import com.srt.message.repository.MemberRepository;
import com.srt.message.utils.encrypt.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.srt.message.config.response.BaseResponseStatus.ALREADY_EXIST_EMAIL;
import static com.srt.message.config.response.BaseResponseStatus.NOT_MATCH_CHECK_PASSWORD;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final MemberRepository memberRepository;

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
}
