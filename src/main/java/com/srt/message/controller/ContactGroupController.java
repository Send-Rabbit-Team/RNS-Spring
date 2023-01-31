package com.srt.message.controller;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.domain.ContactGroup;
import com.srt.message.dto.contact_group.get.GetContactGroupRes;
import com.srt.message.dto.contact_group.ContactGroupDTO;
import com.srt.message.dto.contact_group.patch.PatchContactGroupReq;
import com.srt.message.dto.contact_group.patch.PatchContactGroupRes;
import com.srt.message.dto.contact_group.post.PostContactGroupReq;
import com.srt.message.dto.contact_group.post.PostContactGroupRes;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.service.ContactGroupService;
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
@RequestMapping("/group")
@RequiredArgsConstructor
public class ContactGroupController {

    private final ContactGroupService contactGroupService;

    // 그룹 저장
    @PostMapping("/save")
    public BaseResponse<PostContactGroupRes> saveGroup(@RequestBody PostContactGroupReq postContactGroupReq, HttpServletRequest request) {

        return new BaseResponse<>(contactGroupService.saveContactGroup(postContactGroupReq, JwtInfo.getMemberId(request)));
    }

    // 그룹 수정
    @PatchMapping("/edit")
    public BaseResponse<PatchContactGroupRes> editGroup(@RequestBody PatchContactGroupReq patchContactGroupReq, HttpServletRequest request) {
        PatchContactGroupRes patchContactGroupRes = contactGroupService.editContactGroup(patchContactGroupReq, JwtInfo.getMemberId(request)); // 수정

        return new BaseResponse<>(patchContactGroupRes);
    }

    // 그룹 삭제
    @PatchMapping("/delete/{groupId}")
    public BaseResponse<String> deleteGroup(@PathVariable("groupId") long contactGroupId, HttpServletRequest request) {
        contactGroupService.deleteContactGroup(contactGroupId, JwtInfo.getMemberId(request));

        return new BaseResponse<>("그룹이 정상적으로 삭제 되었습니다.");
    }



    @ApiOperation(
            value = "수신자 그룹 조회",
            notes = "발신자 아이디를 통해 보유한 수신자 그룹을 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
    })
    @GetMapping("/list/{page}")
    public BaseResponse<PageResult<ContactGroup>> getMemberGroup(
            HttpServletRequest request,
            @PathVariable("page") int page) {
        Long memberId = JwtInfo.getMemberId(request);
        return new BaseResponse<>(contactGroupService.getMemberContactGroup(memberId, page));
    }

    // 그룹 찾기
    @ApiOperation(
            value = "그룹 찾기",
            notes = "그룹 찾기 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/{groupId}")
    public BaseResponse<ContactGroupDTO> find(@PathVariable long groupId) {

        return new BaseResponse<>(contactGroupService.findContactGroupById(groupId));
    }

    @ApiOperation(
            value= "그룹 목록 불러오기",
            notes = "그룹 목록 불러오기 API"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @GetMapping("/getAll")
    public BaseResponse<List<ContactGroupDTO>> getAll(HttpServletRequest request){
        return new BaseResponse<>(contactGroupService.getAllContactGroup(JwtInfo.getMemberId((request))));
    }

}
