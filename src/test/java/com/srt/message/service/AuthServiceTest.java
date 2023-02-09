package com.srt.message.service;

import com.srt.message.config.auditor.LoginMember;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.AuthPhoneNumberStatus;
import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Member;
import com.srt.message.domain.SenderNumber;
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
import com.srt.message.repository.SenderNumberRepository;
import com.srt.message.repository.redis.AuthPhoneNumberRedisRepository;
import com.srt.message.utils.encrypt.SHA256;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static com.srt.message.config.response.BaseResponseStatus.ALREADY_EXIST_EMAIL;
import static com.srt.message.config.response.BaseResponseStatus.NOT_AUTH_PHONE_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private SmsService smsService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private AuthPhoneNumberRedisRepository authPhoneNumberRedisRepository;
    @Mock
    private LoginMember loginMember;
    @Mock
    private SenderNumberRepository senderNumberRepository;
    private Member member;

    @BeforeEach
    public void setUpMember(){
        member = Member.builder().id(1).email("forceTlight@gmail.com").
                password("1q2w3e4r!").build();
    }

    @DisplayName("일반 회원가입 (개인)")
    @Test
    void defaultSignUp_Person_Success() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        // given
        PostRegisterReq request = defaultSignUpRequest_person();
        String encryptPassword = SHA256.encrypt(request.getPassword());

        doReturn(member).when(memberRepository).save(any(Member.class));

        // when
        PostRegisterRes response = authService.defaultSignUp(request);

        // then
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(encryptPassword).isEqualTo(request.getPassword());
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getMemberType()).isEqualTo(MemberType.PERSON);
        assertThat(response.getLoginType()).isEqualTo(LoginType.DEFAULT);

        // verify
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @DisplayName("일반 회원가입 (기업)")
    @Test
    void defaultSignUp_Company_Success() {
        // given
        PostRegisterReq request = defaultSignUpRequest_company();
        String encryptPassword = SHA256.encrypt(request.getPassword());

        doReturn(member).when(memberRepository).save(any(Member.class));

        // when
        PostRegisterRes response = authService.defaultSignUp(request);

        // then
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(encryptPassword).isEqualTo(request.getPassword());
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getCompanyName()).isEqualTo(request.getCompanyName());
        assertThat(response.getBsNum()).isEqualTo(request.getBsNum());
        assertThat(response.getKakaoBizId()).isEqualTo(request.getKakaoBizId());
        assertThat(response.getMemberType()).isEqualTo(MemberType.COMPANY);
        assertThat(response.getLoginType()).isEqualTo(LoginType.DEFAULT);

        // verity
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @DisplayName("구글 회원가입 (개인)")
    @Test
    void googleSignUp_Person_Success() {
        // given
        GoogleRegisterReq request = googleSignUpRequest_person();

        doReturn(member).when(memberRepository).save(any(Member.class));

        // when
        GoogleRegisterRes response = authService.googleSignUp(request);

        // then
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getMemberType()).isEqualTo(MemberType.PERSON);
        assertThat(response.getLoginType()).isEqualTo(LoginType.GOOGLE);

        // verity
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @DisplayName("구글 회원가입 (기업)")
    @Test
    void googleSignUp_Company_Success() {
        // given
        GoogleRegisterReq request = googleSignUpRequest_person();

        doReturn(member).when(memberRepository).save(any(Member.class));

        // when
        GoogleRegisterRes response = authService.googleSignUp(request);

        // then
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getCompanyName()).isEqualTo(request.getCompanyName());
        assertThat(response.getBsNum()).isEqualTo(request.getBsNum());
        assertThat(response.getKakaoBizId()).isEqualTo(request.getKakaoBizId());
        assertThat(response.getMemberType()).isEqualTo(MemberType.PERSON);
        assertThat(response.getLoginType()).isEqualTo(LoginType.GOOGLE);

        // verity
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @DisplayName("회원가입 시 이메일 중복 처리")
    @Test
    void signUp_EmailDuplicated() {
        // given
        PostRegisterReq request = PostRegisterReq.builder().email("kakao@gmail.com").build();

        given(memberRepository.findByEmailIgnoreCase(any())).willReturn(Optional.ofNullable(member));

        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                authService.defaultSignUp(request));

        // then
        assertEquals(exception.getStatus(), ALREADY_EXIST_EMAIL);
    }

    @DisplayName("일반 회원가입 시 휴대폰 인증 처리")
    @Test
    void defaultSignUp_authPhoneNumber(){
        // given
        PostRegisterReq request = PostRegisterReq.builder()
                .email("kakao@gmail.com").password("1q2w3e4r!").checkPassword("1q2w3e4r!").build();

        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                authService.defaultSignUp(request));

        assertEquals(exception.getStatus(), NOT_AUTH_PHONE_NUMBER);
    }

    @DisplayName("일반 로그인")
    @Test
    void defaultLogin_Success(){
        // given
        String encryptPassword = SHA256.encrypt(member.getPassword());
        member = Member.builder().id(1).email("forceTlight@gmail.com").
                password(encryptPassword).build();

        PostLoginReq request = PostLoginReq.builder().email("forceTlight@gmail.com").password("1q2w3e4r!").build();
        given(memberRepository.findByEmailIgnoreCase(any())).willReturn(Optional.ofNullable(member));

        JwtInfo jwtInfo = new JwtInfo(member.getId());
        JwtService jwtService = new JwtService();

        // when
        PostLoginRes response = authService.defaultSignIn(request);
        int jwtParseMemberId = (int) jwtService.getJwtInfo(response.getJwt()).get("memberId");

        // then
        assertThat(jwtParseMemberId).isEqualTo(member.getId());
    }

    @DisplayName("구글 로그인")
    @Test
    void googleLogin_Success(){
        // given
        String email = "forceTlight@gmail.com";
        given(memberRepository.findByEmailIgnoreCaseAndLoginType(any(), eq(LoginType.GOOGLE))).willReturn(Optional.ofNullable(member));

        JwtInfo jwtInfo = new JwtInfo(member.getId());
        JwtService jwtService = new JwtService();

        // when
        PostLoginRes response = authService.googleSignIn(email);
        int jwtParseMemberId = (int) jwtService.getJwtInfo(response.getJwt()).get("memberId");

        // then
        assertThat(jwtParseMemberId).isEqualTo(member.getId());
    }

    public PostRegisterReq defaultSignUpRequest_person() {
        PostRegisterReq postRegisterReq = PostRegisterReq.builder()
                .email("kakao@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .name("카카오")
                .phoneNumber("01012341234")
                .memberType(MemberType.PERSON)
                .loginType(LoginType.DEFAULT)
                .build();

        given(authPhoneNumberRedisRepository.findById(any())).willReturn(Optional.ofNullable(AuthPhoneNumber.builder()
                .phoneNumber(postRegisterReq.getPhoneNumber()).authPhoneNumberStatus(AuthPhoneNumberStatus.CONFIRM).build()));

        return postRegisterReq;
    }

    public PostRegisterReq defaultSignUpRequest_company() {
        PostRegisterReq postRegisterReq = PostRegisterReq.builder()
                .email("kakao@gmail.com")
                .password("1q2w3e4r!")
                .checkPassword("1q2w3e4r!")
                .name("카카오")
                .phoneNumber("01012341234")
                .bsNum("12345678901")
                .companyName("카엔프")
                .kakaoBizId("das123")
                .memberType(MemberType.COMPANY)
                .loginType(LoginType.DEFAULT)
                .build();

        given(authPhoneNumberRedisRepository.findById(any())).willReturn(Optional.ofNullable(AuthPhoneNumber.builder()
                .phoneNumber(postRegisterReq.getPhoneNumber()).authPhoneNumberStatus(AuthPhoneNumberStatus.CONFIRM).build()));

        return postRegisterReq;
    }

    public GoogleRegisterReq googleSignUpRequest_person() {
        GoogleRegisterReq googleRegisterReq = GoogleRegisterReq.builder()
                .email("kakao@gmail.com")
                .name("카카오")
                .phoneNumber("01012341234")
                .loginType(LoginType.GOOGLE)
                .memberType(MemberType.PERSON)
                .build();

        return googleRegisterReq;
    }

    public GoogleRegisterReq googleSignUpRequest_company() {
        GoogleRegisterReq googleRegisterReq = GoogleRegisterReq.builder()
                .email("kakao@gmail.com")
                .name("카카오")
                .phoneNumber("01012341234")
                .companyName("카엔프")
                .bsNum("12345678901")
                .kakaoBizId("das123")
                .loginType(LoginType.GOOGLE)
                .memberType(MemberType.COMPANY)
                .build();

        return googleRegisterReq;
    }
    public SenderNumber getSenderNumber(PostRegisterReq request){
        return SenderNumber.builder()
                .member(member)
                .memo(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }
}