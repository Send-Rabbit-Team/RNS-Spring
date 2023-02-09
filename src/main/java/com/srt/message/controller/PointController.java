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

    @GetMapping("/kakaopay/ready")
    public BaseResponse<String> kakaoPaymentReady(HttpServletRequest request, @RequestParam("plusPoint") int plusPoint) {
        return new BaseResponse<>(kakaoPayService.paymentReady(JwtInfo.getMemberId(request), plusPoint));
    }

    @NoIntercept
    @GetMapping("/kakaopay/approve")
    public void kakaoPaymentApprove(@RequestParam("pg_token") String pgToken, HttpServletResponse response) throws IOException {
        if (pgToken != null)
            kakaoPayService.paymentApprove(pgToken);
        response.sendRedirect("http://localhost:3000/admin/profile");
    }

    @GetMapping("/get")
    public BaseResponse<GetPointRes> getPoint(HttpServletRequest request) {
        return new BaseResponse<>(pointService.getPoint(JwtInfo.getMemberId(request)));
    }

    @PatchMapping("/pay")
    public BaseResponse<GetPointRes> payPoint(HttpServletRequest request, @RequestParam("subPoint") int subPoint) {
        return new BaseResponse<>(pointService.payPoint(JwtInfo.getMemberId(request), subPoint));
    }
}
