package com.srt.message.controller;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.domain.SenderNumber;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberReq;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberRes;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.repository.SenderNumberRepository;
import com.srt.message.service.SenderNumberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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


    @ApiOperation(
            value = "발신자 번호 조회",
            notes = "사용자 아이디를 통해 발신자 번호를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2012, message = "존재하지 않는 전화번호입니다.")
    })
    @GetMapping("/list/{page}")
    public BaseResponse<PageResult<RegisterSenderNumberRes, SenderNumber>> getMemberSenderNumber(
            @RequestParam("member_id") long memberId, @PathVariable("page") int page) {
        PageResult<RegisterSenderNumberRes, SenderNumber> memberSenderNumber = senderNumberService.getMemberSenderNumber(memberId, page);
        return new BaseResponse<>(memberSenderNumber);
    }

    @ApiOperation(
            value = "발신자 번호 삭제",
            notes = "발신자 번호 아이디를 통해 발신자 번호를 삭제하는 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2012, message = "존재하지 않는 전화번호입니다."),
            @ApiResponse(code = 2013, message = "권한이 없는 사용자입니다.")
    })
    @PatchMapping("/delete/{senderNumberId}")
    public BaseResponse<String> deleteSenderNumber(@PathVariable long senderNumberId, HttpServletRequest request) {
        Long memberId = JwtInfo.getMemberId(request);
        senderNumberService.deleteSenderNumber(senderNumberId, memberId);
        return new BaseResponse<>("성공");
    }
}
