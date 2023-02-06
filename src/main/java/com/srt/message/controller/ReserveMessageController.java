package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.contact.get.GetContactAllRes;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.service.ReserveMessageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
@RequestMapping("/message/reserve")
@RequiredArgsConstructor
public class ReserveMessageController {
    private final ReserveMessageService reserveMessageService;

    // 예약 수신자 조회
    @ApiOperation(
            value = "예약 수신자 조회",
            notes = "예약된 수신자들을 조회하는 기능이다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 3002, message = "해당 메시지는 예약된 메시지가 아닙니다.")
    })
    @GetMapping("/contact/{messageId}")
    public BaseResponse<GetContactAllRes> getReserveMessageContacts(@PathVariable("messageId") long messageId, HttpServletRequest request) {
        GetContactAllRes response = reserveMessageService.getReserveMessageContacts(messageId);

        log.info("예약 수신자 조회 - memberId: {}, messageId: {}", JwtInfo.getMemberId(request), messageId);

        return new BaseResponse<>(response);
    }

    // 예약 취소
    @ApiOperation(
            value = "예약된 메시지 취소",
            notes = "예약된 메시지를 취소하는 기능이다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2016, message = "해당 사용자의 데이터가 아닙니다."),
            @ApiResponse(code = 2023, message = "존재하는 메시지가 아닙니다.")
    })
    @GetMapping("/cancel/{messageId}")
    public BaseResponse<String> cancelReserveMessage(@PathVariable("messageId") long messageId, HttpServletRequest request) {
        String response = reserveMessageService.cancelReserveMessage(messageId, JwtInfo.getMemberId(request));

        log.info("예약된 메시지 취소 - memberId: {}, postSendMessageReq: {}", JwtInfo.getMemberId(request), messageId);

        return new BaseResponse<>(response);
    }
}
