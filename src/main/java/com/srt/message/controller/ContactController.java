package com.srt.message.controller;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.domain.Contact;
import com.srt.message.service.dto.contact.ContactDTO;
import com.srt.message.service.dto.contact.get.GetContactAllRes;
import com.srt.message.service.dto.contact.get.GetContactRes;
import com.srt.message.service.dto.contact.get.GetGroupContactRes;
import com.srt.message.service.dto.contact.patch.PatchContactReq;
import com.srt.message.service.dto.contact.patch.PatchContactRes;
import com.srt.message.service.dto.contact.post.PostContactReq;
import com.srt.message.service.dto.contact.post.PostContactRes;
import com.srt.message.service.dto.jwt.JwtInfo;
import com.srt.message.service.ContactService;
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
    public BaseResponse<String> deleteContact(@PathVariable long contactId, HttpServletRequest request){
        contactService.deleteContact(contactId, JwtInfo.getMemberId(request));

        return new BaseResponse<>("연락처가 정상적으로 삭제 되었습니다.");
    }

    //연락처에서 그룹 해제
    @ApiOperation(
            value = "연락처에서 그룹 해제",
            notes = "연락처에 연결된 그룹을 해제하는 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2011, message = "존재하지 않는 그룹입니다."),
            @ApiResponse(code = 2014, message = "존재하지 않는 연락처입니다."),
            @ApiResponse(code = 2021, message = "연락처에 연결된 그룹이 아닙니다.")
    })
    @PatchMapping("/quit/{contactId}")
    public BaseResponse<String> quitContactGroup(@PathVariable("contactId") long contactId, HttpServletRequest request){
        contactService.quitContactGroup(contactId, JwtInfo.getMemberId(request));

        return new BaseResponse<>("연락처가 그룹에서 해제 되었습니다.");
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
    public BaseResponse<PageResult<ContactDTO, Contact>> search(@PathVariable int currentPage, @RequestParam String phoneNumber, HttpServletRequest request){
        return new BaseResponse<>(contactService.searchContact(phoneNumber,currentPage,JwtInfo.getMemberId(request)));
    };

    // 그룹으로 연락처 찾기
    @ApiOperation(
            value = "그룹으로 연락처 찾기",
            notes = "그룹으로 연락처 찾기 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/byGroup")
    public BaseResponse<List<GetGroupContactRes>> filterByGroup(@RequestParam long groupId, HttpServletRequest request){
        return new BaseResponse<>(contactService.filterContactByGroup(groupId, JwtInfo.getMemberId(request)));
    }

    // 아이디로 연락처 찾기
    @GetMapping("/{contactId}")
    public BaseResponse<ContactDTO> find(@PathVariable int contactId){
        return new BaseResponse<>(contactService.findContactById(contactId));
    }

    // 연락처 전체 조회
    @ApiOperation(
            value = "연락처 전체 조회 (페이징)",
            notes = "사용자 아이디를 통해 사용자가 보유한 모든 연락처를 조회하는 API - 페이징 처리"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/list/{page}")
    public BaseResponse<PageResult<GetContactRes, Contact>> getMemberContactListPaging(
            HttpServletRequest request,
            @PathVariable("page") int page) {
        PageResult<GetContactRes, Contact> memberContactList = contactService.getMemberContact(JwtInfo.getMemberId(request), page);
        return new BaseResponse<>(memberContactList);
    }

    @ApiOperation(
            value = "연락처 전체 조회",
            notes = "사용자 아이디를 통해 사용자가 보유한 모든 연락처를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/list")
    public BaseResponse<GetContactAllRes> getMemberContactList(HttpServletRequest request){
        GetContactAllRes getContactAllRes = contactService.getMemberContactAll(JwtInfo.getMemberId(request));
        return new BaseResponse<>(getContactAllRes);
    }


}
