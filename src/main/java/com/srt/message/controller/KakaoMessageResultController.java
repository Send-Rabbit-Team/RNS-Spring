package com.srt.message.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.config.type.ButtonType;
import com.srt.message.config.type.KmsgSearchType;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.kakao_message.get.GetKakaoMessageRes;
import com.srt.message.dto.kakao_message_result.get.GetKakaoMessageResultListRes;
import com.srt.message.service.kakao.KakaoMessageResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
@RequestMapping("/kakao/message/result")
@RequiredArgsConstructor
public class KakaoMessageResultController {
    private final KakaoMessageResultService kakaoMessageResultService;

    @GetMapping("/{page}")
    public BaseResponse<PageResult<GetKakaoMessageRes>> getAllKakaoMessage(
            @PathVariable int page,
            HttpServletRequest request) {
        return new BaseResponse<>(kakaoMessageResultService.getAllKakaoMessage(page, JwtInfo.getMemberId(request)));
    }

    @GetMapping("/filter/{page}")
    public BaseResponse<PageResult<GetKakaoMessageRes>> getKakaoMessageByButtonType(
            @PathVariable int page,
            @RequestParam("type") String buttonType,
            HttpServletRequest request) {
        return new BaseResponse<>(kakaoMessageResultService.getKakaoMessageByButtonType(page, JwtInfo.getMemberId(request), ButtonType.valueOf(buttonType)));
    }

    @GetMapping("/search/{page}")
    public BaseResponse<PageResult<GetKakaoMessageRes>> getSearchKakaoMessage(
            @PathVariable int page,
            @RequestParam("type") String searchType,
            @RequestParam("keyword") String keyword,
            HttpServletRequest request) {
        return new BaseResponse<>(kakaoMessageResultService.getSearchKakaoMessage(page, JwtInfo.getMemberId(request), KmsgSearchType.valueOf(searchType), keyword));
    }

    @GetMapping("/info/{messageId}")
    public BaseResponse<GetKakaoMessageResultListRes> getKakaoMessageResult(
            @PathVariable Long messageId,
            HttpServletRequest request) throws JsonProcessingException {
        return new BaseResponse<>(kakaoMessageResultService.getKakaoMessageResult(JwtInfo.getMemberId(request), messageId));
    }

}
