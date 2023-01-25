package com.srt.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.AuthPhoneNumberStatus;
import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import com.srt.message.domain.redis.AuthPhoneNumber;
import com.srt.message.service.dto.auth.login.post.PostLoginReq;
import com.srt.message.service.dto.auth.login.post.PostLoginRes;
import com.srt.message.service.dto.auth.register.google.GoogleRegisterReq;
import com.srt.message.service.dto.auth.register.google.GoogleRegisterRes;
import com.srt.message.service.dto.auth.register.post.PostRegisterReq;
import com.srt.message.service.dto.auth.register.post.PostRegisterRes;
import com.srt.message.service.dto.jwt.JwtInfo;
import com.srt.message.jwt.JwtService;
import com.srt.message.repository.MemberRepository;
import com.srt.message.repository.redis.AuthPhoneNumberRedisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;

import static com.srt.message.config.response.BaseResponseStatus.ALREADY_EXIST_EMAIL;
import static com.srt.message.config.response.BaseResponseStatus.NOT_AUTH_PHONE_NUMBER;
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
    private AuthPhoneNumberRedisRepository authPhoneNumberRedisRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // 회원 가입 테스트 (개인)
    @Test
    public void defaultSignUp_SavePersonMember_True(){
        // given
        PostRegisterReq req = PostRegisterReq.builder()
                .email("qeasd123asd@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .name("김형준")
                .phoneNumber("01012341234")
                .memberType(MemberType.PERSON)
                .loginType(LoginType.DEFAULT)
                .build();

        saveAuthPhoneNumber("01012341234");

        // when
        PostRegisterRes res = authService.defaultSignUp(req);
        long memberId = memberRepository.findByEmailIgnoreCase(req.getEmail()).get().getId();

        // then
        assertThat(memberId).isEqualTo(res.getMemberId());
    }

    // 회원 가입 테스트 (기업)
    @Test
    public void defaultSignUp_SaveCompanyMember_True(){
        // given
        PostRegisterReq req = PostRegisterReq.builder()
                .email("qeasd123asd@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .name("김형준")
                .companyName("카카오 엔터프라이즈")
                .bsNum("1234512345")
                .phoneNumber("01012341234")
                .memberType(MemberType.COMPANY)
                .loginType(LoginType.DEFAULT)
                .build();

        saveAuthPhoneNumber("01012341234");

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
                .email("qeasd123asd@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .name("김형준")
                .companyName("카카오 엔터프라이즈")
                .bsNum("1234512345")
                .phoneNumber("01012341234")
                .memberType(MemberType.COMPANY)
                .loginType(LoginType.DEFAULT)
                .build();

        saveAuthPhoneNumber("01012341234");

        authService.defaultSignUp(req1);

        PostRegisterReq req2 = PostRegisterReq.builder()
                .email("qeasd123asd@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .name("김형준")
                .companyName("카카오 엔터프라이즈")
                .bsNum("1234512345")
                .phoneNumber("01012341235")
                .memberType(MemberType.COMPANY)
                .loginType(LoginType.DEFAULT)
                .build();

        saveAuthPhoneNumber("01012341235");

        // when
        BaseException exception = assertThrows(BaseException.class,
                () -> authService.defaultSignUp(req2));

        // then
        assertThat(exception.getStatus()).isEqualTo(ALREADY_EXIST_EMAIL);
    }

    // 휴대폰 미 인증시 예외 테스트
    @Test
    public void Should_ThrowBaseException_When_NotAuthPhoneNumber(){
        // given
        PostRegisterReq req = PostRegisterReq.builder()
                .email("qeasd123asd@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .name("김형준")
                .companyName("카카오 엔터프라이즈")
                .bsNum("1234512345")
                .phoneNumber("01012341234")
                .memberType(MemberType.COMPANY)
                .loginType(LoginType.DEFAULT)
                .build();

        // when
        BaseException exception = assertThrows(BaseException.class,
                () -> authService.defaultSignUp(req));

        // then
        assertThat(exception.getStatus()).isEqualTo(NOT_AUTH_PHONE_NUMBER);
    }

    // 일반 로그인 테스트
    @Test
    public void defaultSignIn_getJwtAndParseMemberId_True(){
        // given
        initDefaultMember();

        PostLoginReq req = PostLoginReq.builder()
                .email("qeasd123asd@gmail.com")
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

    // 구글 회원가입 테스트 (개인)
    @Test
    public void googleSignUp_SavePersonMember_True(){
        // given
        GoogleRegisterReq req = GoogleRegisterReq.builder()
                .email("t123e23st123")
                .name("오영주")
                .phoneNumber("01012341234")
                .memberType(MemberType.COMPANY)
                .loginType(LoginType.GOOGLE)
                .build();

        // when
        GoogleRegisterRes res = authService.googleSignUp(req);
        long memberId = memberRepository.findByEmailIgnoreCase(req.getEmail()).get().getId();

        // then
        assertThat(memberId).isEqualTo(res.getMemberId());
    }

    // 구글 회원가입 테스트 (기업)
    @Test
    public void googleSignUp_SaveCompanyMember_True(){
        // given
        GoogleRegisterReq req = GoogleRegisterReq.builder()
                .email("t123e23st123")
                .name("오영주")
                .phoneNumber("01012341234")
                .companyName("네이버")
                .bsNum("0123401234")
                .memberType(MemberType.COMPANY)
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

        String email = "t123e23st123@gmail.com";

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
                .email("qeasd123asd@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .name("김형준")
                .phoneNumber("01012341234")
                .memberType(MemberType.PERSON)
                .loginType(LoginType.DEFAULT)
                .build();

        saveAuthPhoneNumber("01012341234");

        authService.defaultSignUp(req);
    }

    // 구글 멤버 초기호출
    public void initGoogleMember(){
        GoogleRegisterReq req = GoogleRegisterReq.builder()
                .email("t123e23st123@gmail.com")
                .name("오영주")
                .phoneNumber("01012341234")
                .companyName("네이버")
                .bsNum("0123401234")
                .memberType(MemberType.COMPANY)
                .loginType(LoginType.GOOGLE)
                .build();

        authService.googleSignUp(req);
    }

    // 레디스 휴대전화 정보 저장
    public void saveAuthPhoneNumber(String phoneNumber){
        AuthPhoneNumber authPhoneNumber = AuthPhoneNumber.builder()
                .phoneNumber("01012341234")
                .authPhoneNumberStatus(AuthPhoneNumberStatus.CONFIRM)
                .build();
        authPhoneNumberRedisRepository.save(authPhoneNumber);
    }
}