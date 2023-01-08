package com.srt.message.controller;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.sms.MessageDTO;
import com.srt.message.dto.sms.PhoneNumberValidDTO;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.srt.message.config.response.BaseResponseStatus.*;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsController {
    private final SmsService smsService;

    @NoIntercept
    @PostMapping("/send")
    public BaseResponse<String> sendSms(@RequestBody MessageDTO messageDTO){
        try {
            smsService.sendSms(messageDTO);
        }catch (Exception e){
            throw new BaseException(SEND_MESSAGE_ERROR);
        }

        return new BaseResponse<>(SEND_MESSAGE_SUCCESS);
    }

    @NoIntercept
    @PostMapping("/valid")
    public BaseResponse<String> sendSms(@RequestBody PhoneNumberValidDTO phoneNumberValidDTO){
        smsService.validAuthToken(phoneNumberValidDTO);

        return new BaseResponse<>(PHONE_NUMBER_AUTH_SUCCESS);
    }
}
