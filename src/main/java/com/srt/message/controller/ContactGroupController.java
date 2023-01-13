package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.dto.contact.patch.PatchContactRes;
import com.srt.message.dto.contact_group.patch.PatchContactGroupReq;
import com.srt.message.dto.contact_group.patch.PatchContactGroupRes;
import com.srt.message.dto.contact_group.post.PostContactGroupReq;
import com.srt.message.dto.contact_group.post.PostContactGroupRes;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.ContactGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class ContactGroupController {

    private final ContactGroupService contactGroupService;

    // 그룹 저장
    @PostMapping("/save")
    @NoIntercept
    public BaseResponse<PostContactGroupRes> saveGroup(@RequestBody PostContactGroupReq postContactGroupReq, HttpServletRequest request){
        PostContactGroupRes postContactGroupRes = contactGroupService.saveContactGroup(postContactGroupReq, JwtInfo.getMemberId(request));

        return new BaseResponse<>(postContactGroupRes);
    }

    // 그룹 수정
    @PostMapping("/edit")
    @NoIntercept
    public BaseResponse<PatchContactGroupRes> editGroup(@RequestBody PatchContactGroupReq patchContactGroupReq,  HttpServletRequest request){
        PatchContactGroupRes patchContactGroupRes = contactGroupService.editContactGroup(patchContactGroupReq, JwtInfo.getMemberId(request)); // 수정

        return new BaseResponse<>(patchContactGroupRes);
    }

    // 그룹 삭제
    @PostMapping("/delete/{groupId}")
    @NoIntercept
    public BaseResponse<String> deleteGroup(@PathVariable long contactGroupId, HttpServletRequest request){
        contactGroupService.deleteContactGroup(contactGroupId, JwtInfo.getMemberId(request));

        return new BaseResponse<>("그룹이 정상적으로 삭제 되었습니다.");
    }
}