package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.message_rule.get.GetSMSRuleRes;
import com.srt.message.dto.message_rule.post.PostSMSRuleReq;
import com.srt.message.dto.message_rule.post.PostSMSRuleRes;
import com.srt.message.service.MessageRuleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/msg/rule")
@RequiredArgsConstructor
public class MessageRuleController {
    private final MessageRuleService messageRuleService;

    @ApiOperation(
            value = "SMS 메시지 분배 발송 규칙 생성",
            notes = "KT, SKT, LG 3개 중계사의 분배 발송 규칙을 생성한다. \n 3개 중계사 비율의 총 합은 반드시 100이 되어야한다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2019, message = "중계사 비율 설정이 올바르지 않습니다."),
            @ApiResponse(code = 2020, message = "존재하지 않는 중계사입니다.")
    })
    @PostMapping("/create")
    public BaseResponse<PostSMSRuleRes> createMessageRule(PostSMSRuleReq postSMSRuleReq, HttpServletRequest request){
       PostSMSRuleRes postSMSRuleRes = messageRuleService.createSMSRule(postSMSRuleReq, JwtInfo.getMemberId(request));

       return new BaseResponse<>(postSMSRuleRes);
    }

    @ApiOperation(
            value = "SMS 메시지 분배 발송 규칙 불러오기",
            notes = "중계사 분배 발송 규칙을 모두 불러온다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2020, message = "존재하지 않는 중계사입니다.")
    })
    @GetMapping("/getAll")
    public BaseResponse<List<GetSMSRuleRes>> getAll(HttpServletRequest request){
        List<GetSMSRuleRes> getSMSRuleRes = messageRuleService.getAll(JwtInfo.getMemberId(request));
        return new BaseResponse<>(getSMSRuleRes);
    }

}
