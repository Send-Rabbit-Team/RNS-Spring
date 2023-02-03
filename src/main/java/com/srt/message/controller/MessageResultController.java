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
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Log4j2
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
        log.info("발송된 메시지 조회 - memberId: {}, postSendMessageReq: {}", JwtInfo.getMemberId(request), page);

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
    public BaseResponse<List<GetMessageResultRes>> getMessageResultsById(@PathVariable("messageId") long messageId
            , HttpServletRequest request) throws JsonProcessingException {
        List<GetMessageResultRes> messageResultRes = messageResultService.getMessageResultsById(messageId);
        log.info("메시지 처리 결과 조회 - memberId: {}, messageId: {}", JwtInfo.getMemberId(request), messageId);

        return new BaseResponse<>(messageResultRes);
    }

    @ApiOperation(
            value = "메시지 유형별 필터 조회",
            notes = "발송한 메시지들을 사용자가 선택한 유형으로 필터 조회하는 기능이다. (SMS / LMS / MMS)"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/filter/type/{page}")
    public BaseResponse<List<GetMessageRes>> getMessagesByType(@PathVariable("page") int page, @RequestParam("type") String type, HttpServletRequest request) {
        List<GetMessageRes> messageResList = messageResultService.getMessagesByType(type, JwtInfo.getMemberId(request), page);
        log.info("메시지 유형별 필터 조회 - memberId: {}, type: {}", JwtInfo.getMemberId(request), type);

        return new BaseResponse<>(messageResList);
    }

    @ApiOperation(
            value = "예약된 메시지 필터 조회",
            notes = "예약된 메시지들을 필터 조회하는 기능이다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/filter/reserve/{page}")
    public BaseResponse<List<GetMessageRes>> getReserveMessages(@PathVariable("page") int page, HttpServletRequest request) {
        List<GetMessageRes> messageResList = messageResultService.getReserveMessages(JwtInfo.getMemberId(request), page);
        log.info("예약된 메시지 필터 조회 - memberId: {}, page: {}", JwtInfo.getMemberId(request), page);

        return new BaseResponse<>(messageResList);
    }

    @ApiOperation(
            value = "메시지 검색 조회",
            notes = "메시지 목록들을 검색을 통해 조회할 수 있는 기능이다. (수신자 번호, 발신자 번호, 연락처 메모, 메시지 내용 키워드 조회 기능을 제공)"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/search/{page}")
    public BaseResponse<List<GetMessageRes>> getMessagesBySearching(@PathVariable("page") int page, @RequestParam(value = "type") String searchType
            , @RequestParam("keyword") String keyword, HttpServletRequest request) {
        List<GetMessageRes> messageResList = messageResultService.getMessageBySearching(searchType, keyword, JwtInfo.getMemberId(request), page);
        log.info("메시지 검색 조회 - memberId: {}, type: {}, keyword: {}", JwtInfo.getMemberId(request), searchType, keyword);

        return new BaseResponse<>(messageResList);
    }
}
