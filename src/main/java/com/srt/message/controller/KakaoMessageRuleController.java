package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.service.KakaoMessageRuleService;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.kakaoMessageRule.get.GetKakaoMessageRuleRes;
import com.srt.message.dto.kakaoMessageRule.patch.PatchKakaoMessageRuleReq;
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

    @GetMapping("/get")
    public BaseResponse<List<GetKakaoMessageRuleRes>> createKakaoMessageRuleService(
            HttpServletRequest request) {
        return new BaseResponse<>(kakaoMessageRuleService.getKakaoMessageRule(JwtInfo.getMemberId(request)));
    }

    @PatchMapping("/edit")
    public BaseResponse<List<GetKakaoMessageRuleRes>> editKakaoMessageRuleService(
            @RequestBody List<PatchKakaoMessageRuleReq> patchKakaoMessageRuleReqList,
            HttpServletRequest request) {
        return new BaseResponse<>(kakaoMessageRuleService.editKakaoMessageRule(patchKakaoMessageRuleReqList, JwtInfo.getMemberId(request)));
    }
}
