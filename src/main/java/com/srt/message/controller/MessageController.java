package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.service.KakaoMessageService;
import com.srt.message.service.dto.jwt.JwtInfo;
import com.srt.message.service.dto.message.kakao.post.PostSendKakaoMessageReq;
import com.srt.message.service.dto.message.post.PostSendMessageReq;
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
    public BaseResponse<String> imageUploadTest(@RequestParam("image") MultipartFile multipartFile){
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
    public BaseResponse<String> sendMessage(@RequestBody PostSendMessageReq postSendMessageReq, HttpServletRequest request){
        String processTime = messageService.sendMessageToBroker(postSendMessageReq, JwtInfo.getMemberId(request));

        return new BaseResponse<>("메시지 갯수: " + postSendMessageReq.getCount() + ", 메시지 발송 걸린 시간: " + Double.parseDouble(processTime) / 1000 + "초");
    }

    @ApiOperation(
            value = "중계사 알림톡 전송",
            notes = "설정된 중계사 비율 값을 참조하여서 각 중계사에 알림톡을 발송한다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @PostMapping("/send/kakao")
    public BaseResponse<String> sendKakaoMessage(@RequestBody PostSendKakaoMessageReq postSendKakaoMessageReq, HttpServletRequest request){
        String processTime = kakaoMessageService.sendKakaoMessageToBroker(postSendKakaoMessageReq, JwtInfo.getMemberId(request));

        return new BaseResponse<>("메시지 갯수: " + postSendKakaoMessageReq.getCount() + ", 메시지 발송 걸린 시간: " + Double.parseDouble(processTime) / 1000 + "초");
    }
}
