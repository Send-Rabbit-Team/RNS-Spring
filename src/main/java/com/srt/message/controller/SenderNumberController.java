package com.srt.message.controller;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.domain.SenderNumber;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.sender_number.get.GetSenderNumberRes;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberReq;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberRes;
import com.srt.message.service.SenderNumberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/sender")
public class SenderNumberController {
    private final SenderNumberService senderNumberService;

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
    public BaseResponse<RegisterSenderNumberRes> registerSenderNumber(
            HttpServletRequest request,
            @RequestBody RegisterSenderNumberReq registerSenderNumberReq){
        Long memberId = JwtInfo.getMemberId(request);
        RegisterSenderNumberRes registerSenderNumberRes = senderNumberService.registerSenderNumber(memberId, registerSenderNumberReq);

        log.info("SenderNumber-register: ", registerSenderNumberRes.getPhoneNumber());

        return new BaseResponse<>(registerSenderNumberRes);
    }


    @ApiOperation(
            value = "발신자 번호 조회 (페이징 O)",
            notes = "사용자 아이디를 통해 발신자 번호를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2012, message = "존재하지 않는 전화번호입니다.")
    })
    @GetMapping("/{page}")
    public BaseResponse<PageResult<GetSenderNumberRes>> getPageSenderNumber(
            HttpServletRequest request,
            @PathVariable("page") int page) {
        return new BaseResponse<>(senderNumberService.getPageSenderNumber(JwtInfo.getMemberId(request), page));
    }

    @ApiOperation(
            value = "발신자 번호 조회 (페이징 X)",
            notes = "사용자 아이디를 통해 발신자 번호를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2012, message = "존재하지 않는 전화번호입니다.")
    })
    @GetMapping("/all")
    public BaseResponse<List<GetSenderNumberRes>> getAllSenderNumber(HttpServletRequest request) {
        return new BaseResponse<>(senderNumberService.getAllSenderNumber(JwtInfo.getMemberId(request)));
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
        senderNumberService.deleteSenderNumber(senderNumberId, JwtInfo.getMemberId(request));
        return new BaseResponse<>("성공");
    }

    @ApiOperation(
            value = "차단 번호 조회",
            notes = "발신자 번호를 통해 수신 치단 번호를 조회할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2012, message = "존재하지 않는 전화번호입니다."),
            @ApiResponse(code = 2017, message = "존재하지 않는 발신자 번호입니다.")
    })
    @GetMapping("/block/{senderNumberId}")
    public BaseResponse<String> getBlockNumber(@PathVariable long senderNumberId, HttpServletRequest request){

        return new BaseResponse<>(senderNumberService.getBlockNumber(senderNumberId,JwtInfo.getMemberId(request)));
    }
}
