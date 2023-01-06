package com.srt.message.advice;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.config.response.BaseResponseStatus;
import com.srt.message.config.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionAdvice {
    private final MessageSource ms;

    // @Valid 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<ErrorResponse> MemberNotFoundException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        String[] codes = bindingResult.getAllErrors().get(0).getCodes();

        String code = codes[1];
        ErrorResponse errorResponse = new ErrorResponse(code);
        return new BaseResponse<>(errorResponse);
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<BaseResponseStatus> baseException(BaseException e) {
        log.warn("Handle CommonException: {}", e.getStatus());
        return new BaseResponse<>(e.getStatus());
    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Exception.class)
//    public BaseResponse<BaseResponseStatus> allHandleException(Exception e) {
//        log.error("Handle All Exception: {}", e.getMessage());
//        return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
//    }
}