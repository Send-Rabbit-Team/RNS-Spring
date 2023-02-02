package com.srt.message.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.message.get.GetMessageRes;
import com.srt.message.dto.message_result.get.GetMessageResultRes;
import com.srt.message.service.MessageResultService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message/result")
public class MessageResultController {
    private final MessageResultService messageResultService;

    @ApiOperation(
            value = "발송된 메시지 조회",
            notes = "발송된 메시지들을 페이징 형태로 조회한다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/{page}")
    public BaseResponse<PageResult<GetMessageRes>> getMessagesByPaging(@PathVariable("page") int page, HttpServletRequest request) {
        PageResult<GetMessageRes> messageRes = messageResultService.getAllMessages(JwtInfo.getMemberId(request), page);

        return new BaseResponse<>(messageRes);
    }

    @ApiOperation(
            value = "메시지 처리 결과 조회",
            notes = "메시지 처리 결과들을 조회한다. 만약에, 상태 DB에 저장되있을 경우 레디스에서 불러오고, 아니면" +
                    "RDBMS에서 조회한다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/info/{messageId}")
    public BaseResponse<List<GetMessageResultRes>> getMessageResultsById(@PathVariable("messageId") long messageId) throws JsonProcessingException {
        List<GetMessageResultRes> messageResultRes = messageResultService.getMessageResultsById(messageId);

        return new BaseResponse<>(messageResultRes);
    }

    @ApiOperation(
            value = "메시지 유형별 필터 조회",
            notes = "발송한 메시지들을 사용자가 선택한 유형으로 필터 조회하는 기능이다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/filter/{page}")
    public BaseResponse<List<GetMessageRes>> getMessagesByType(@PathVariable("page") int page, @RequestParam(value = "type") String type, HttpServletRequest request) {
        List<GetMessageRes> messageResList = messageResultService.getMessagesByType(type, JwtInfo.getMemberId(request), page);

        return new BaseResponse<>(messageResList);
    }
}
