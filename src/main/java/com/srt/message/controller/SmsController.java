package com.srt.message.controller;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.sms.MessageDTO;
import com.srt.message.dto.sms.PhoneNumberValidDTO;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.SmsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Log4j2
@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsController {
    private final SmsService smsService;

    @ApiOperation(
            value = "핸드폰 인증 문자 전송",
            notes = "인증 문자를 보내는 API이다. content 필드는 null 값이 가능하다."
    )
    @ApiResponses({
            @ApiResponse(code = 2501, message = "인증 번호 발송에 성공했습니다."),
            @ApiResponse(code = 3001, message = "메시지를 발송하는 과정 중 오류가 발생했습니다.")
    })
    @NoIntercept
    @PostMapping("/send")
    public BaseResponse<String> sendSms(@RequestBody MessageDTO messageDTO){
        try {
            smsService.sendSms(messageDTO);
        }catch (Exception e){
            throw new BaseException(SEND_MESSAGE_ERROR);
        }
        log.info("핸드폰 인증 문자 전송 - phoneNumber: {}", messageDTO.getTo());

        return new BaseResponse<>(SEND_MESSAGE_SUCCESS);
    }

    @ApiOperation(
            value = "휴대폰 인증 번호 확인",
            notes = "휴대폰 인증 번호를 입력하고 검증 과정을 거치는 API이다."
    )
    @ApiResponses({
            @ApiResponse(code = 2502, message = "핸드폰 번호 인증에 성공하였습니다."),
            @ApiResponse(code = 2007, message = "유효하지 않은 인증번호입니다.")
    })
    @NoIntercept
    @PostMapping("/valid")
    public BaseResponse<String> sendSms(@RequestBody PhoneNumberValidDTO phoneNumberValidDTO){
        smsService.validAuthToken(phoneNumberValidDTO);
        log.info("핸드폰 인증 번호 확인 - phoneNumber: {}, valid: {}", phoneNumberValidDTO.getPhoneNumber(), phoneNumberValidDTO.getAuthToken());

        return new BaseResponse<>(PHONE_NUMBER_AUTH_SUCCESS);
    }
}
