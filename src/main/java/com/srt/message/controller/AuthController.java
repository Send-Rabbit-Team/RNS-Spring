package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.auth.login.post.PostLoginReq;
import com.srt.message.dto.auth.login.post.PostLoginRes;
import com.srt.message.dto.auth.register.google.GoogleRegisterReq;
import com.srt.message.dto.auth.register.google.GoogleRegisterRes;
import com.srt.message.dto.auth.register.post.PostRegisterReq;
import com.srt.message.dto.auth.register.post.PostRegisterRes;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.member.get.GetInfoMemberRes;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private final GoogleOAuthService googleOAuthService;

    // 회원가입
    @ApiOperation(
            value = "일반 회원가입",
            notes = "일반 회원가입을 통해서 사용자 정보를 등록할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "이미 존재하는 이메일입니다."),
            @ApiResponse(code = 2004, message = "비밀번호 확인란을 다시 입력해주시기 바랍니다."),
            @ApiResponse(code = 2008, message = "인증이 되지 않은 전화번호 입니다.")
    })
    @PostMapping("/register")
    @NoIntercept
    public BaseResponse<PostRegisterRes> defaultSignUp(@RequestBody @Validated PostRegisterReq postRegisterReq){
        PostRegisterRes postRegisterRes = authService.defaultSignUp(postRegisterReq);
        log.info("Default Sign-Up - email: {}" + postRegisterRes.getEmail());

        return new BaseResponse<>(postRegisterRes);
    }

    // 로그인
    @ApiOperation(
            value = "일반 로그인",
            notes = "일반 로그인을 통해서 JWT를 반환 받는다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2004, message = "존재하지 않는 이메일 주소입니다."),
            @ApiResponse(code = 2005, message = "비밀번호가 일치하지 않습니다.")
    })
    @PostMapping("/login")
    @NoIntercept
    public BaseResponse<PostLoginRes> defaultSignIn(@RequestBody PostLoginReq postLoginReq){
        PostLoginRes postLoginRes = authService.defaultSignIn(postLoginReq);
        log.info("Default Sign-In - jwt: {}" + postLoginRes.getJwt());

        return new BaseResponse<>(postLoginRes);
    }

    @ApiIgnore
    @NoIntercept
    @GetMapping("/google")
    public void getGoogleAuthUrl(HttpServletResponse response) throws Exception {
        response.sendRedirect(googleOAuthService.getOauthRedirectURL());
    }

    @ApiIgnore
    @NoIntercept
    @GetMapping("/google/redirect")
    public BaseResponse<Object> googleRedirect(@RequestParam(name="code") String code) throws IOException {
        Object response = googleOAuthService.getGoogleRedirectURL(code);
        return new BaseResponse<>(response);
    }

    // 구글 회원가입
    @ApiOperation(
            value = "구글 회원가입",
            notes = "구글 회원가입을 통해서 사용자 정보를 등록할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "이미 존재하는 이메일입니다.")
    })
    @NoIntercept
    @PostMapping("/google/register")
    public BaseResponse<GoogleRegisterRes> googleSignUp(@RequestBody GoogleRegisterReq googleRegisterReq){
        GoogleRegisterRes googleRegisterRes = authService.googleSignUp(googleRegisterReq);
        log.info("Google Sign-Up - email: {}" + googleRegisterRes.getEmail());

        return new BaseResponse<>(googleRegisterRes);
    }

    // 구글 로그인
    @ApiOperation(
            value = "구글 로그인",
            notes = "구글 로그인을 통해서 JWT를 반환 받는다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2004, message = "존재하지 않는 이메일 주소입니다.")
    })
    @NoIntercept
    @PostMapping("/google/login")
    public BaseResponse<PostLoginRes> googleSignIn(@RequestParam String email){
        PostLoginRes postLoginRes = authService.googleSignIn(email);
        log.info("Google Sign-In - jwt: {}" + postLoginRes.getJwt());

        return new BaseResponse<>(postLoginRes);
    }

    // 자동 로그인, 사용자 정보 가져오기
    @ApiOperation(
            value = "JWT로 사용자 정보 가져오기",
            notes = "JWT를 통해 사용자의 정보를 가져온다"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자 입니다.")
    })
    @GetMapping("/userinfo")
    public BaseResponse<GetInfoMemberRes> getUserInfoByJwt(HttpServletRequest request){
        Long memberId = JwtInfo.getMemberId(request);

        GetInfoMemberRes getInfoMemberRes = authService.getUserInfoByJwt(memberId);
        log.info("userInfo - memberId: {}", memberId);

        return new BaseResponse<>(getInfoMemberRes);
    }
}
