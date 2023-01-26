package com.srt.message.controller;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.domain.KakaoTemplate;
import com.srt.message.service.KakaoTemplateService;
import com.srt.message.service.dto.jwt.JwtInfo;
import com.srt.message.service.dto.kakaoTemplate.get.GetKakaoTemplateRes;
import com.srt.message.service.dto.kakaoTemplate.patch.PatchKakaoTemplateReq;
import com.srt.message.service.dto.kakaoTemplate.post.PostKakaoTemplateReq;
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
@RequestMapping("/kakao")
@RequiredArgsConstructor
public class KakaoTemplateController {
    private final KakaoTemplateService kakaoTemplateService;

    @ApiOperation(
            value = "탬플릿 저장",
            notes = "새로운 탬플릿을 저장할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
    })
    @PostMapping("/template/register")
    public BaseResponse<GetKakaoTemplateRes> registerTemplate(@RequestBody PostKakaoTemplateReq postKakaoTemplateReq, HttpServletRequest request){
        return new BaseResponse<>(kakaoTemplateService.registerKakaoTemplate(JwtInfo.getMemberId(request), postKakaoTemplateReq));
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
    public BaseResponse<GetKakaoTemplateRes> getOneTemplate(@PathVariable("templateId") long templateId, HttpServletRequest request){
        return new BaseResponse<>(kakaoTemplateService.getOneKakaoTemplate(JwtInfo.getMemberId(request), templateId));
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
    public BaseResponse<List<GetKakaoTemplateRes>> getAllTemplate(HttpServletRequest request){
        return new BaseResponse<>(kakaoTemplateService.getAllKakaoTemplate(JwtInfo.getMemberId(request)));
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
    public BaseResponse<PageResult<GetKakaoTemplateRes, KakaoTemplate>> getPageTemplate(@PathVariable("page") int page, HttpServletRequest request){
        return new BaseResponse<>(kakaoTemplateService.getPageKakaoTemplate(JwtInfo.getMemberId(request), page));
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
    public BaseResponse<GetKakaoTemplateRes> editTemplate(@RequestBody PatchKakaoTemplateReq patchKakaoTemplateReq, HttpServletRequest request) {
        return new BaseResponse<>(kakaoTemplateService.editKakaoTemplate(JwtInfo.getMemberId(request), patchKakaoTemplateReq));
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
    public BaseResponse<GetKakaoTemplateRes> deleteTemplate(@PathVariable("templateId") Long templateId, HttpServletRequest request) {
        return new BaseResponse<>(kakaoTemplateService.deleteKakaoTemplate(JwtInfo.getMemberId(request), templateId));
    }

}
