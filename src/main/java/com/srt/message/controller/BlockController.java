package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.block.get.GetBlockRes;
import com.srt.message.dto.block.post.PostBlockReq;
import com.srt.message.dto.block.post.PostBlockRes;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.BlockService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
@RequiredArgsConstructor
public class BlockController {
    private final BlockService blockService;

    // 차단 번호 등록
    @ApiOperation(
            value = "수신 차단",
            notes = "receiveNumber - 수신자 전화번호, blockNumber - 수신자 차단 번호"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2014, message = "존재하지 않는 연락처입니다."),
            @ApiResponse(code = 2028, message = "존재하지 않는 수신 차단 번호입니다."),
            @ApiResponse(code = 2029, message = "이미 차단된 번호입니다.")
    })
    @PostMapping("/block/register")
    @NoIntercept
    public BaseResponse<PostBlockRes> registerBlock(@RequestBody PostBlockReq postBlockReq) {
        PostBlockRes postRegisterRes = blockService.registerBlock(postBlockReq);
        log.info("수신자 차단 - receiveNumber: {}, senderNumber: {}", postRegisterRes.getReceiveNumber(), postRegisterRes.getSenderNumber());

        return new BaseResponse<>(postRegisterRes);
    }

    // 수신 차단한 사람 조회
    @ApiOperation(
            value = "수신 차단번호 조회",
            notes = "발신자 번호로 수신 차단한 번호들을 반환한다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2016, message = "해당 사용자의 데이터가 아닙니다."),
            @ApiResponse(code = 2017, message = "존재하지 않는 발신자 번호입니다."),
    })
    @GetMapping("/blocks")
    public BaseResponse<GetBlockRes> registerBlock(@RequestParam String senderNumber, HttpServletRequest request){
        GetBlockRes getBlockRes = blockService.getBlockByNumber(senderNumber, JwtInfo.getMemberId(request));
        log.info("수신 차단번호 조회 - memberId: {}, senderNumber: {}", JwtInfo.getMemberId(request), getBlockRes.getReceiveNumbers());

        return new BaseResponse<>(getBlockRes);
    }
}
