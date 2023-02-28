package com.srt.message.controller;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.template.get.GetTemplateRes;
import com.srt.message.dto.template.patch.PatchTemplateReq;
import com.srt.message.dto.template.post.PostTemplateReq;
import com.srt.message.service.message.TemplateService;
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
public class TemplateController {
    private final TemplateService templateService;

    @ApiOperation(
            value = "메시지 탬플릿 저장",
            notes = "새로운 메시지 탬플릿을 저장할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
    })
    @PostMapping("/template/register")
    public BaseResponse<GetTemplateRes> registerTemplate(@RequestBody PostTemplateReq postTemplateReq, HttpServletRequest request){
        log.info("메시지 탬플릿 저장 - memberId: {}, postTemplateReq: {}", JwtInfo.getMemberId(request), postTemplateReq);

        return new BaseResponse<>(templateService.registerTemplate(JwtInfo.getMemberId(request), postTemplateReq));
    }

    @ApiOperation(
            value = "탬플릿 단일 조회",
            notes = "탬플릿 아이디를 통해 해당 탬플릿을 조회할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2022, message = "존재하지 않는 탬플릿입니다."),
            @ApiResponse(code = 2018, message = "권한이 없는 사용자입니다."),
    })
    @GetMapping("/template/{templateId}")
    public BaseResponse<GetTemplateRes> getOneTemplate(@PathVariable("templateId") long templateId, HttpServletRequest request){
        log.info("메시지 탬플릿 단일 조회 - memberId: {}, templateId: {}", JwtInfo.getMemberId(request), templateId);

        return new BaseResponse<>(templateService.getOneTemplate(JwtInfo.getMemberId(request), templateId));
    }

    @ApiOperation(
            value = "사용자 탬플릿 조회 (페이징 X)",
            notes = "사용자 아이디를 통해 사용자가 보유한 모든 탬플릿을 조회할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2022, message = "존재하지 않는 탬플릿입니다."),
    })
    @GetMapping("/templates/all")
    public BaseResponse<List<GetTemplateRes>> getAllTemplate(HttpServletRequest request){
        log.info("사용자 탬플릿 조회- memberId: {}", JwtInfo.getMemberId(request));

        return new BaseResponse<>(templateService.getAllTemplate(JwtInfo.getMemberId(request)));
    }

    @ApiOperation(
            value = "사용자 탬플릿 조회 (페이징 O)",
            notes = "사용자 아이디를 통해 사용자가 보유한 모든 탬플릿을 조회할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2022, message = "존재하지 않는 탬플릿입니다."),
    })
    @GetMapping("/templates/{page}")
    public BaseResponse<PageResult<GetTemplateRes>> getPageTemplate(@PathVariable("page") int page, HttpServletRequest request){
        log.info("사용자 탬플릿 페이징 조회- memberId: {}, page: {}", JwtInfo.getMemberId(request), page);

        return new BaseResponse<>(templateService.getPageTemplate(JwtInfo.getMemberId(request), page));
    }

    @ApiOperation(
            value = "사용자 탬플릿 수정",
            notes = "기존 탬플릿을 수정할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2022, message = "존재하지 않는 탬플릿입니다."),
            @ApiResponse(code = 2018, message = "권한이 없는 사용자입니다."),
    })
    @PatchMapping("/template/edit")
    public BaseResponse<GetTemplateRes> editTemplate(@RequestBody PatchTemplateReq patchTemplateReq, HttpServletRequest request) {
        log.info("사용자 탬플릿 페이징 수정 - memberId: {}, patchTemplateReq: {}", JwtInfo.getMemberId(request), patchTemplateReq);

        return new BaseResponse<>(templateService.editTemplate(JwtInfo.getMemberId(request), patchTemplateReq));
    }

    @ApiOperation(
            value = "사용자 탬플릿 삭제",
            notes = "사용자 아이디와 탬플릿 아이디를 통해 사용자가 보유한 탬플릿을 삭제할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2022, message = "존재하지 않는 탬플릿입니다."),
            @ApiResponse(code = 2018, message = "권한이 없는 사용자입니다."),
    })
    @PatchMapping("/template/delete/{templateId}")
    public BaseResponse<GetTemplateRes> deleteTemplate(@PathVariable("templateId") Long templateId, HttpServletRequest request) {
        GetTemplateRes getTemplateRes = templateService.deleteTemplate(JwtInfo.getMemberId(request), templateId);

        log.info("사용자 탬플릿 페이징 삭제 - memberId: {}, templateId: {}", JwtInfo.getMemberId(request), templateId);
        return new BaseResponse<>(getTemplateRes);
    }

}
