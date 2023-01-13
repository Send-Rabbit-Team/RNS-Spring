package com.srt.message.controller;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.domain.Contact;
import com.srt.message.dto.contact.ContactDTO;
import com.srt.message.dto.contact.patch.PatchContactReq;
import com.srt.message.dto.contact.patch.PatchContactRes;
import com.srt.message.dto.contact.post.PostContactReq;
import com.srt.message.dto.contact.post.PostContactRes;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.ContactService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    // 연락처 저장
    @ApiOperation(
            value = "연락처 저장",
            notes = "연락처 저장을 통해서 새로운 대상을 연락처에 저장할 수 있다."
    )
    @ApiResponses({
            @ApiResponse(code = 2011, message = "존재하지 않는 그룹입니다."),
            @ApiResponse(code = 2012, message = "이미 등록된 연락처입니다."),
    })
    @PostMapping("/create")
    public BaseResponse<PostContactRes> saveContact(@RequestBody PostContactReq postContactReq, HttpServletRequest request){
        PostContactRes postContactRes = contactService.saveContact(postContactReq, JwtInfo.getMemberId(request));

        return new BaseResponse<>(postContactRes);
    }

    // 연락처 수정
    @ApiOperation(
            value = "연락처 수정",
            notes = "저장된 연락처를 수정하는 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2014, message = "존재하지 않는 연락처입니다."),
            @ApiResponse(code = 2016, message = "해당 사용자의 데이터가 아닙니다.")
    })
    @PatchMapping("/edit")
    @NoIntercept
    public BaseResponse<PatchContactRes> editContact(@RequestBody PatchContactReq patchContactReq, HttpServletRequest request){
        PatchContactRes patchContactRes = contactService.editContact(patchContactReq, JwtInfo.getMemberId(request)); // 수정

        return new BaseResponse<>(patchContactRes);
    }


    //연락처 삭제
    @ApiOperation(
            value = "연락처 삭제",
            notes = "저장된 연락처를 삭제하는 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2011, message = "존재하지 않는 그룹입니다."),
            @ApiResponse(code = 2012, message = "이미 등록된 연락처입니다."),
    })
    @PatchMapping("/delete/{contactId}")
    @NoIntercept
    public BaseResponse<String> deleteContact(@PathVariable long contactId, HttpServletRequest request){
        contactService.deleteContact(contactId, JwtInfo.getMemberId(request));

        return new BaseResponse<>("연락처가 정상적으로 삭제 되었습니다.");
    }


    // 연락처 검색
    @ApiOperation(
            value = "연락처 검색 (페이징)",
            notes = "연락처 검색 API - 페이징 처리"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/search/{currentPage}")
    public BaseResponse<PageResult<ContactDTO, Contact>> search(@PathVariable int currentPage, @RequestParam String phoneNumber){
        return new BaseResponse<>(contactService.searchContact(phoneNumber,currentPage));
    };

    // 연락처 그룹 필터링
    @ApiOperation(
            value = "연락처 그룹 필터링 (페이징)",
            notes = "연락처 그룹 필터링 API - 페이징 처리"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    // memberId 별 조회 필요!
    @GetMapping("/byGroup/{currentPage}")
    @NoIntercept
    public BaseResponse<PageResult<ContactDTO, Contact>> filterByGroup(@PathVariable int currentPage,@RequestParam long groupId){
        return new BaseResponse<>(contactService.filterContactByGroup(groupId,currentPage));
    }
}
