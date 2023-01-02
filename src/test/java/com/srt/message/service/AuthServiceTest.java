package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.type.BsType;
import com.srt.message.config.type.LoginType;
import com.srt.message.dto.auth.register.post.PostRegisterReq;
import com.srt.message.dto.auth.register.post.PostRegisterRes;
import com.srt.message.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        long memberId = memberRepository.findMemberByEmail(req.getEmail()).get().getId();

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
}