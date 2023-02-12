package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.service.KakaoMessageRuleService;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.kakaoMessageRule.get.GetKakaoMessageRuleRes;
import com.srt.message.dto.kakaoMessageRule.patch.PatchKakaoMessageRuleReq;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/kakao/msg/rule")
@RequiredArgsConstructor
public class KakaoMessageRuleController {
    private final KakaoMessageRuleService kakaoMessageRuleService;

    @ApiOperation(
            value = "알림톡 브로커 전송 규칙 불러오기",
            notes = "알림톡 브로커들의 전송 규칙들을 불러온다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2026, message = "존재하지 않는 발송 규칙입니다.")
    })
    @GetMapping("/get")
    public BaseResponse<List<GetKakaoMessageRuleRes>> getKakaoMessageRules(
            HttpServletRequest request) {
        log.info("알림톡 브로커 전송 규칙 불러오기 - memberId: {}", JwtInfo.getMemberId(request));

        return new BaseResponse<>(kakaoMessageRuleService.getKakaoMessageRule(JwtInfo.getMemberId(request)));
    }

    @ApiOperation(
            value = "알림톡 브로커 전송 규칙 수정하기",
            notes = "알림톡 브로커들의 전송 규칙들을 수정한다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2026, message = "존재하지 않는 발송 규칙입니다.")
    })
    @PatchMapping("/edit")
    public BaseResponse<List<GetKakaoMessageRuleRes>> editKakaoMessageRules(
            @RequestBody List<PatchKakaoMessageRuleReq> patchKakaoMessageRuleReqList,
            HttpServletRequest request) {
        log.info("알림톡 브로커 전송 규칙 수정하기 - memberId: {}", JwtInfo.getMemberId(request));

        return new BaseResponse<>(kakaoMessageRuleService.editKakaoMessageRule(patchKakaoMessageRuleReqList, JwtInfo.getMemberId(request)));
    }
}
