package com.srt.message.controller;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.service.kakao.KakaoTemplateService;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.dto.kakaoTemplate.get.GetKakaoTemplateRes;
import com.srt.message.dto.kakaoTemplate.patch.PatchKakaoTemplateReq;
import com.srt.message.dto.kakaoTemplate.post.PostKakaoTemplateReq;
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
            value = "알림톡 탬플릿 저장",
            notes = "새로운 알림톡 탬플릿을 저장할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
    })
    @PostMapping("/template/register")
    public BaseResponse<GetKakaoTemplateRes> registerTemplate(@RequestBody PostKakaoTemplateReq postKakaoTemplateReq, HttpServletRequest request){
        log.info("알림톡 탬플릿 저장 - memberId: {}, title: {}", JwtInfo.getMemberId(request), postKakaoTemplateReq.getTitle());

        return new BaseResponse<>(kakaoTemplateService.registerKakaoTemplate(JwtInfo.getMemberId(request), postKakaoTemplateReq));
    }

    @ApiOperation(
            value = "알림톡 탬플릿 단일 조회",
            notes = "알림톡 탬플릿 아이디를 통해 해당 탬플릿을 조회할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2022, message = "존재하지 않는 탬플릿입니다."),
            @ApiResponse(code = 2018, message = "권한이 없는 사용자입니다."),
    })
    @GetMapping("/template/{templateId}")
    public BaseResponse<GetKakaoTemplateRes> getOneTemplate(@PathVariable("templateId") long templateId, HttpServletRequest request){
        log.info("알림톡 탬플릿 단일 조회 - memberId: {}, templateId: {}", JwtInfo.getMemberId(request), templateId);

        return new BaseResponse<>(kakaoTemplateService.getOneKakaoTemplate(JwtInfo.getMemberId(request), templateId));
    }

    @ApiOperation(
            value = "사용자 알림톡 탬플릿 조회 (페이징 X)",
            notes = "사용자 아이디를 통해 사용자가 보유한 모든 알림톡 탬플릿을 조회할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2022, message = "존재하지 않는 탬플릿입니다."),
    })
    @GetMapping("/templates/all")
    public BaseResponse<List<GetKakaoTemplateRes>> getAllTemplate(HttpServletRequest request){
        log.info("알림톡 탬플릿 모두 조회 - memberId: {}", JwtInfo.getMemberId(request));

        return new BaseResponse<>(kakaoTemplateService.getAllKakaoTemplate(JwtInfo.getMemberId(request)));
    }

    @ApiOperation(
            value = "사용자 알림톡 탬플릿 조회 (페이징 O)",
            notes = "사용자 아이디를 통해 사용자가 보유한 모든 알림톡 탬플릿을 조회할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2022, message = "존재하지 않는 탬플릿입니다."),
    })
    @GetMapping("/templates/{page}")
    public BaseResponse<PageResult<GetKakaoTemplateRes>> getPageTemplate(@PathVariable("page") int page, HttpServletRequest request){
        log.info("알림톡 탬플릿 페이징 조회 - memberId: {}, page: {}", JwtInfo.getMemberId(request), page);

        return new BaseResponse<>(kakaoTemplateService.getPageKakaoTemplate(JwtInfo.getMemberId(request), page));
    }

    @ApiOperation(
            value = "사용자 알림톡 탬플릿 수정",
            notes = "기존 알림톡 탬플릿을 수정할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2022, message = "존재하지 않는 탬플릿입니다."),
            @ApiResponse(code = 2018, message = "권한이 없는 사용자입니다."),
    })
    @PatchMapping("/template/edit")
    public BaseResponse<GetKakaoTemplateRes> editTemplate(@RequestBody PatchKakaoTemplateReq patchKakaoTemplateReq, HttpServletRequest request) {
        log.info("알림톡 탬플릿 수정 - memberId: {}, templateId: {}", JwtInfo.getMemberId(request), patchKakaoTemplateReq.getTemplateId());

        return new BaseResponse<>(kakaoTemplateService.editKakaoTemplate(JwtInfo.getMemberId(request), patchKakaoTemplateReq));
    }

    @ApiOperation(
            value = "사용자 알림톡 탬플릿 삭제",
            notes = "사용자 아이디와 알림톡 탬플릿 아이디를 통해 사용자가 보유한 알림톡 탬플릿을 삭제할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2009, message = "존재하지 않는 사용자입니다."),
            @ApiResponse(code = 2022, message = "존재하지 않는 탬플릿입니다."),
            @ApiResponse(code = 2018, message = "권한이 없는 사용자입니다."),
    })
    @PatchMapping("/template/delete/{templateId}")
    public BaseResponse<GetKakaoTemplateRes> deleteTemplate(@PathVariable("templateId") Long templateId, HttpServletRequest request) {
        log.info("알림톡 탬플릿 수정 - memberId: {}, templateId: {}", JwtInfo.getMemberId(request), templateId);

        return new BaseResponse<>(kakaoTemplateService.deleteKakaoTemplate(JwtInfo.getMemberId(request), templateId));
    }

}
