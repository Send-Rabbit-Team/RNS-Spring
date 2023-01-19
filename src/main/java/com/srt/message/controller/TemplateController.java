package com.srt.message.controller;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.domain.Template;
import com.srt.message.service.dto.jwt.JwtInfo;
import com.srt.message.service.dto.template.get.GetTemplateRes;
import com.srt.message.service.dto.template.patch.PatchTemplateReq;
import com.srt.message.service.dto.template.post.PostTemplateReq;
import com.srt.message.service.TemplateService;
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
@RequestMapping("/template")
public class TemplateController {
    private final TemplateService templateService;

    @ApiOperation(
            value = "탬플릿 저장",
            notes = "새로운 탬플릿을 저장할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
    })
    @PostMapping("/register")
    public BaseResponse<GetTemplateRes> registerTemplate(@RequestBody PostTemplateReq postTemplateReq, HttpServletRequest request){
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
    @GetMapping("/get")
    public BaseResponse<GetTemplateRes> getOneTemplate(@RequestParam("templateId") long templateId, HttpServletRequest request){
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
    @GetMapping("/list")
    public BaseResponse<List<GetTemplateRes>> getAllTemplate(HttpServletRequest request){
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
    @GetMapping("/all/{page}")
    public BaseResponse<PageResult<GetTemplateRes, Template>> getPageTemplate(@PathVariable("page") int page, HttpServletRequest request){
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
    @PatchMapping("/edit")
    public BaseResponse<GetTemplateRes> editTemplate(@RequestBody PatchTemplateReq patchTemplateReq, HttpServletRequest request) {
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
    @PatchMapping("/delete/{templateId}")
    public BaseResponse<GetTemplateRes> deleteTemplate(@PathVariable("templateId") Long templateId, HttpServletRequest request) {
        GetTemplateRes getTemplateRes = templateService.deleteTemplate(JwtInfo.getMemberId(request), templateId);
        return new BaseResponse<>(getTemplateRes);
    }

}
