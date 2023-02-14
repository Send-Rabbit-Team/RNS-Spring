package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.service.kakao.KakaoMessageService;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.kakao_message.post.PostKakaoMessageReq;
import com.srt.message.dto.message.post.PostSendMessageReq;
import com.srt.message.service.message.MessageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    private final KakaoMessageService kakaoMessageService;

    // 중계사에 문자 전송
    @ApiOperation(
            value = "중계사 문자 전송",
            notes = "설정된 중계사 비율 값을 참조하여서 각 중계사에 문자를 발송한다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @PostMapping("/send/sms")
    public BaseResponse<String> sendMessage(@RequestBody PostSendMessageReq postSendMessageReq, HttpServletRequest request) {
        String response = messageService.sendMessageToBroker(postSendMessageReq, JwtInfo.getMemberId(request));
        if (response.startsWith("예약성공"))
            return new BaseResponse<>("성공적으로 예약 발송 되었습니다.");

        log.info("중계사 문자 전송 - memberId: {}, postSendMessageReq: {}", JwtInfo.getMemberId(request), postSendMessageReq.getMessage());

        return new BaseResponse<>("메시지 갯수: " + postSendMessageReq.getCount() + ", 메시지 발송 걸린 시간: " + Double.parseDouble(response) / 1000 + "초");
    }

    @ApiOperation(
            value = "중계사 알림톡 전송",
            notes = "설정된 중계사 비율 값을 참조하여서 각 중계사에 알림톡을 발송한다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @PostMapping("/send/kakao")
    public BaseResponse<String> sendKakaoMessage(@RequestBody PostKakaoMessageReq postKakaoMessageReq, HttpServletRequest request) {
        String processTime = kakaoMessageService.sendKakaoMessageToBroker(postKakaoMessageReq, JwtInfo.getMemberId(request));

        log.info("중계사 알림톡 전송 - memberId: {}, postSendMessageReq: {}", JwtInfo.getMemberId(request), postKakaoMessageReq);

        return new BaseResponse<>("메시지 갯수: " + postKakaoMessageReq.getCount() + ", 메시지 발송 걸린 시간: " + Double.parseDouble(processTime) / 1000 + "초");
    }
}
