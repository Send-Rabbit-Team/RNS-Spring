package com.srt.message.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.message.get.GetMessageRes;
import com.srt.message.dto.message_result.get.GetMessageResultRes;
import com.srt.message.service.KakaoMessageService;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.message.kakao.post.PostSendKakaoMessageReq;
import com.srt.message.dto.message.post.PostSendMessageReq;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.MessageService;
import com.srt.message.service.ObjectStorageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.FILE_UPLOAD_SUCCESS;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final ObjectStorageService objectStorageService;

    private final MessageService messageService;

    private final KakaoMessageService kakaoMessageService;

    // MMS 이미지 전송 테스트
    @ApiOperation(
            value = "MMS 이미지 전송 테스트",
            notes = "Object Storage에 이미지가 잘 저장되는지 테스트"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @PostMapping("/upload/test")
    @NoIntercept
    public BaseResponse<String> imageUploadTest(@RequestParam("image") MultipartFile multipartFile) {
        objectStorageService.uploadImageTest(multipartFile);

        return new BaseResponse<>(FILE_UPLOAD_SUCCESS);
    }

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
    public BaseResponse<String> sendKakaoMessage(@RequestBody PostSendKakaoMessageReq postSendKakaoMessageReq, HttpServletRequest request) {
        String processTime = kakaoMessageService.sendKakaoMessageToBroker(postSendKakaoMessageReq, JwtInfo.getMemberId(request));

        return new BaseResponse<>("메시지 갯수: " + postSendKakaoMessageReq.getCount() + ", 메시지 발송 걸린 시간: " + Double.parseDouble(processTime) / 1000 + "초");
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
    @GetMapping("/reserve/cancel/{messageId}")
    public BaseResponse<String> cancelReserveMessage(@PathVariable("messageId") long messageId, HttpServletRequest request) {
        String response = messageService.cancelReserveMessage(messageId, JwtInfo.getMemberId(request));

        return new BaseResponse<>(response);
    }
}
