package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.point.get.GetPointRes;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.KakaoPayService;
import com.srt.message.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {
    private final KakaoPayService kakaoPayService;
    private final PointService pointService;

    @GetMapping("/charge")
    public BaseResponse<String> chargePoint(
            HttpServletRequest request,
            @RequestParam("smsPoint") int smsPoint,
            @RequestParam("kakaoPoint") int kakaoPoint) {
        return new BaseResponse<>(kakaoPayService.paymentReady(JwtInfo.getMemberId(request), smsPoint, kakaoPoint));
    }

    @NoIntercept
    @GetMapping("/redirect")
    public void kakaoPaymentApprove(@RequestParam("pg_token") String pgToken, HttpServletResponse response) throws IOException {
        if (pgToken != null)
            kakaoPayService.paymentApprove(pgToken);
        response.sendRedirect("http://localhost:3000/admin/profile");
    }

    @GetMapping("/get")
    public BaseResponse<GetPointRes> getPoint(HttpServletRequest request) {
        return new BaseResponse<>(pointService.getPoint(JwtInfo.getMemberId(request)));
    }

    @GetMapping("/valid")
    public BaseResponse<GetPointRes> validPoint(
            HttpServletRequest request,
            @RequestParam("smsPoint") int smsPoint,
            @RequestParam("kakaoPoint") int kakaoPoint) {
        return new BaseResponse<>(pointService.validPoint(JwtInfo.getMemberId(request), smsPoint, kakaoPoint));
    }
}
