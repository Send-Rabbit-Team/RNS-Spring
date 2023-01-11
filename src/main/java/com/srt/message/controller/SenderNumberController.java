package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberReq;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberRes;
import com.srt.message.repository.SenderNumberRepository;
import com.srt.message.service.SenderNumberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/sender")
public class SenderNumberController {
    private final SenderNumberService senderNumberService;

    @GetMapping("/test")
    public void auditTest(){
        senderNumberService.testSave();
    }

    @ApiOperation(
            value = "발신자 번호 등록",
            notes = "발신자 번호 등록 전에 휴대폰 인증 작업이 있어야합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "이미 등록된 전화번호 입니다."),
            @ApiResponse(code = 2007, message = "유효하지 않은 인증번호입니다.")
    })
    @PostMapping("/register")
    public BaseResponse<RegisterSenderNumberRes> registerSenderNumber(@RequestBody RegisterSenderNumberReq registerSenderNumberReq){
        RegisterSenderNumberRes registerSenderNumberRes = senderNumberService.registerSenderNumber(registerSenderNumberReq);

        log.info("SenderNumber-register: ", registerSenderNumberRes.getPhoneNumber());

        return new BaseResponse<>(registerSenderNumberRes);
    }
}
