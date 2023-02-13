package com.srt.message.controller;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.kakao_message_reserve.get.GetKakaoMessageReserveContactRes;
import com.srt.message.dto.kakao_message_reserve.get.GetKakaoMessageReserveRes;
import com.srt.message.service.kakao.KakaoMessageReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/kakao/message/reserve")
@RequiredArgsConstructor
public class KakaoMessageReserveController {
    private final KakaoMessageReserveService kakaoMessageReserveService;

    @GetMapping("/{page}")
    public BaseResponse<PageResult<GetKakaoMessageReserveRes>> getKakaoMessageReserveList(
            HttpServletRequest request,
            @PathVariable("page") int page) {
        return new BaseResponse<>(kakaoMessageReserveService.getKakaoMessageReserveList(page, JwtInfo.getMemberId(request)));
    }

    @GetMapping("/contact/{messageId}")
    public BaseResponse<List<GetKakaoMessageReserveContactRes>> getKakaoMessageReserveContactList(
            HttpServletRequest request,
            @PathVariable("messageId") long messageId) {
        return new BaseResponse<>(kakaoMessageReserveService.getKakaoMessageReserveContactList(JwtInfo.getMemberId(request), messageId));
    }

    @PatchMapping("/cancel/{messageId}")
    public BaseResponse<GetKakaoMessageReserveRes> cancelKakaoMessageReserve(
            HttpServletRequest request,
            @PathVariable("messageId") long messageId) {
        return new BaseResponse<>(kakaoMessageReserveService.cancelKakaoMessageReserve(JwtInfo.getMemberId(request), messageId));
    }
}
