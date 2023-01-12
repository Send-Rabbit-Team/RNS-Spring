package com.srt.message.controller;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @PostMapping("/save")
    @NoIntercept
    public BaseResponse<PostContactRes> saveContact(@RequestBody PostContactReq postContactReq, HttpServletRequest request){
        PostContactRes postContactRes = contactService.saveContact(postContactReq, JwtInfo.getMemberId(request));

        return new BaseResponse<>(postContactRes);
    }

    // 연락처 수정
    @PatchMapping("/edit")
    @NoIntercept
    public BaseResponse<PatchContactRes> editContact(@RequestBody PatchContactReq patchContactReq, HttpServletRequest request){
        PatchContactRes patchContactRes = contactService.editContact(patchContactReq, JwtInfo.getMemberId(request)); // 수정

        return new BaseResponse<>(patchContactRes);
    }


    //연락처 삭제
    @PatchMapping("/delete/{contactId}")
    @NoIntercept
    public BaseResponse<String> deleteContact(@PathVariable long contactId, HttpServletRequest request){
        contactService.deleteContact(contactId, JwtInfo.getMemberId(request));

        return new BaseResponse<>("삭제가 되었습니다.");
    }


    // 연락처 검색
    @GetMapping("/search/{currentPage}")
    @NoIntercept
    public Page<ContactDTO> search(@PathVariable int currentPage, @RequestParam String phoneNumber){
        return contactService.search(phoneNumber,currentPage);
    };
}
