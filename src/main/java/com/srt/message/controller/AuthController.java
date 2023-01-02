package com.srt.message.controller;

import com.srt.message.dto.auth.register.post.PostRegisterReq;
import com.srt.message.dto.auth.register.post.PostRegisterRes;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.AuthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

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
}
