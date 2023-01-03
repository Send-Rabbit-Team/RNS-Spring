package com.srt.message.controller;

import com.srt.message.dto.auth.register.google.GoogleRegisterReq;
import com.srt.message.dto.auth.register.google.GoogleRegisterRes;
import com.srt.message.dto.auth.register.google.GoogleUserInfoDTO;
import com.srt.message.dto.auth.register.post.PostRegisterReq;
import com.srt.message.dto.auth.register.post.PostRegisterRes;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

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
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @PostMapping("/register")
    @NoIntercept
    public PostRegisterRes defaultSignUp(@RequestBody PostRegisterReq postRegisterReq){
        PostRegisterRes postRegisterRes = authService.defaultSignUp(postRegisterReq);
        log.info("Default Sign-Up: " + postRegisterRes.getEmail());

        return postRegisterRes;
    }

    @NoIntercept
    @GetMapping("/google")
    public void getGoogleAuthUrl(HttpServletResponse response) throws Exception {
        response.sendRedirect(googleOAuthService.getOauthRedirectURL());
    }

    @NoIntercept
    @GetMapping("/google/userinfo")
    public GoogleUserInfoDTO getGoogleUserInfo(@RequestParam(name="code") String code) throws IOException {
        GoogleUserInfoDTO googleUserInfoDTO = googleOAuthService.getGoogleUserInfo(code);
        return googleUserInfoDTO;
    }

    @PostMapping("/google/register")
    public GoogleRegisterRes googleSignUp(@RequestBody GoogleRegisterReq googleRegisterReq){
        GoogleRegisterRes googleRegisterRes = authService.googleSignUp(googleRegisterReq);
        log.info("Google Sign-Up: " + googleRegisterRes.getEmail());

        return googleRegisterRes;
    }
}
