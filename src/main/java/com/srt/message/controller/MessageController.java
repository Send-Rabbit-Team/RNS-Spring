package com.srt.message.controller;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.config.response.BaseResponseStatus;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.ObjectStorageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.srt.message.config.response.BaseResponseStatus.FILE_UPLOAD_SUCCESS;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final ObjectStorageService objectStorageService;

    // MMS 이미지 전송 테스트
    @ApiOperation(
            value = "MMS 이미지 전송 테스트",
            notes = "Object Storage에 이미지가 잘 저장되는지 테스트"
    )
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다.")
    })
    @PostMapping("/upload/test")
    @NoIntercept
    public BaseResponse<String> imageUploadTest(@RequestParam("image") MultipartFile multipartFile){
        objectStorageService.uploadImageTest(multipartFile);

        return new BaseResponse<>(FILE_UPLOAD_SUCCESS);
    }
}
