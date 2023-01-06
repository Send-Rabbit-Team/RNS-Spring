package com.srt.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.type.BsType;
import com.srt.message.config.type.LoginType;
import com.srt.message.dto.auth.login.post.PostLoginReq;
import com.srt.message.dto.auth.login.post.PostLoginRes;
import com.srt.message.dto.auth.register.google.GoogleRegisterReq;
import com.srt.message.dto.auth.register.google.GoogleRegisterRes;
import com.srt.message.dto.auth.register.post.PostRegisterReq;
import com.srt.message.dto.auth.register.post.PostRegisterRes;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.jwt.JwtService;
import com.srt.message.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;

import static com.srt.message.config.response.BaseResponseStatus.ALREADY_EXIST_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class AuthServiceTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // 회원 가입 테스트
    @Test
    public void defaultSignUp_SaveMember_True(){
        // given
        PostRegisterReq req = PostRegisterReq.builder()
                .email("forceTlight@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .address("판교")
                .ceoName("이길여")
                .companyName("가천대학교")
                .bsNum("12345678901")
                .bsType(BsType.IT)
                .loginType(LoginType.DEFAULT)
                .build();

        // when
        PostRegisterRes res = authService.defaultSignUp(req);
        long memberId = memberRepository.findByEmailIgnoreCase(req.getEmail()).get().getId();

        // then
        assertThat(memberId).isEqualTo(res.getMemberId());
    }

    // 이메일 중복 방지 테스트
    @Test
    public void Should_ThrowBaseException_When_AlreadyExistEmail(){
        // given
        PostRegisterReq req1 = PostRegisterReq.builder()
                .email("forceTlight@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .address("판교")
                .ceoName("이길여")
                .companyName("가천대학교")
                .bsNum("12345678901")
                .bsType(BsType.IT)
                .loginType(LoginType.DEFAULT)
                .build();

        authService.defaultSignUp(req1);

        PostRegisterReq req2 = PostRegisterReq.builder()
                .email("forceTlight@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .address("판교")
                .ceoName("이길여")
                .companyName("가천대학교")
                .bsNum("12345678901")
                .bsType(BsType.IT)
                .loginType(LoginType.DEFAULT)
                .build();

        // when
        BaseException exception = assertThrows(BaseException.class,
                () -> authService.defaultSignUp(req2));

        // then
        assertThat(exception.getStatus()).isEqualTo(ALREADY_EXIST_EMAIL);
    }

    // 일반 로그인 테스트
    @Test
    public void defaultSignIn_getJwtAndParseMemberId_True(){
        // given
        initDefaultMember();

        PostLoginReq req = PostLoginReq.builder()
                .email("test@gmail.com")
                .password("1q2w3e4r!")
                .build();

        // when
        PostLoginRes res = authService.defaultSignIn(req);
        String accessToken = res.getJwt();

        JwtService jwtService = new JwtService();
        LinkedHashMap jwtInfo = jwtService.getJwtInfo(accessToken);
        JwtInfo convertJwtInfo = objectMapper.convertValue(jwtInfo, JwtInfo.class);

        // then
        assertThat(res.getMemberId()).isEqualTo(convertJwtInfo.getMemberId());
    }

    // 구글 회원가입 테스트
    @Test
    public void googleSignUp_SaveMember_True(){
        // given
        GoogleRegisterReq req = GoogleRegisterReq.builder()
                .email("test@gmail.com")
                .address("판교")
                .ceoName("김형준")
                .companyName("코다리")
                .bsNum("12345678901")
                .bsType(BsType.IT)
                .loginType(LoginType.GOOGLE)
                .build();

        // when
        GoogleRegisterRes res = authService.googleSignUp(req);
        long memberId = memberRepository.findByEmailIgnoreCase(req.getEmail()).get().getId();

        // then
        assertThat(memberId).isEqualTo(res.getMemberId());
    }

    // 구글 로그인 테스트
    @Test
    public void googleSignIn_getJwtAndParseMemberId_True(){
        // given
        initGoogleMember();

        String email = "test@gmail.com";

        // when
        PostLoginRes res = authService.googleSignIn(email);
        String accessToken = res.getJwt();

        JwtService jwtService = new JwtService();
        LinkedHashMap jwtInfo = jwtService.getJwtInfo(accessToken);
        JwtInfo convertJwtInfo = objectMapper.convertValue(jwtInfo, JwtInfo.class);

        // then
        assertThat(res.getMemberId()).isEqualTo(convertJwtInfo.getMemberId());
    }

    // 일반 멤버 초기호출
    public void initDefaultMember(){
        PostRegisterReq req = PostRegisterReq.builder()
                .email("test@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .address("판교")
                .ceoName("이길여")
                .companyName("가천대학교")
                .bsNum("12345678901")
                .bsType(BsType.IT)
                .loginType(LoginType.DEFAULT)
                .build();

        authService.defaultSignUp(req);
    }

    // 구글 멤버 초기호출
    public void initGoogleMember(){
        GoogleRegisterReq req = GoogleRegisterReq.builder()
                .email("test@gmail.com")
                .address("판교")
                .ceoName("김형준")
                .companyName("코다리")
                .bsNum("12345678901")
                .bsType(BsType.IT)
                .loginType(LoginType.GOOGLE)
                .build();

        authService.googleSignUp(req);
    }
}