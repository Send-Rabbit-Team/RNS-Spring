package com.srt.message.config.response;

import lombok.Getter;

/**
 * 상태 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    EMPTY_JWT(false, 2001, "header에 JWT가 없습니다."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다. 재로그인 바랍니다."),
    EXPIRED_JWT(false, 2003,"만료기간이 지난 JWT입니다. 재로그인 바랍니다."),
    ALREADY_EXIST_EMAIL(false, 2003,"이미 존재하는 이메일입니다."),
    NOT_MATCH_CHECK_PASSWORD(false, 2004,"비밀번호 확인란을 다시 확인해주시기 바랍니다."),
    NOT_EXIST_EMAIL(false, 2005,"존재하지 않는 이메일 주소입니다."),
    NOT_MATCH_PASSWORD(false, 2006,"비밀번호가 일치하지 않습니다."),
    INVALID_AUTH_TOKEN(false, 2007,"유효하지 않은 인증번호입니다."),
    NOT_AUTH_PHONE_NUMBER(false, 2008,"인증이 되지 않은 전화번호입니다."),
    ALREADY_AUTH_PHONE_NUMBER(false, 2008,"이미 인증을 받은 전화번호입니다."),
    NOT_EXIST_MEMBER(false, 2009,"존재하지 않는 사용자입니다."),
    ALREADY_EXIST_PHONE_NUMBER(false, 2010,"이미 등록된 휴대전화 번호입니다."),
    NOT_EXIST_GROUP(false, 2011,"존재하지 않는 그룹입니다."),
    ALREADY_EXIST_CONTACT_NUMBER(false, 2012,"이미 등록된 연락처입니다."),
    NOT_EXIST_CONTACT_NUMBER(false, 2012,"존재하지 않는 연락처입니다."),
    NOT_ACCESS_GOOGLE(false, 2011, "구글 회원 인증에 실패했습니다."),



    /**
     * 2500 : Request 성공
     */
    FILE_UPLOAD_SUCCESS(true, 2500, "파일 업로드에 성공하였습니다."),
    SEND_MESSAGE_SUCCESS(true, 2501, "인증 번호 발송에 성공했습니다."),
    PHONE_NUMBER_AUTH_SUCCESS(true, 2502, "핸드폰 번호 인증에 성공하였습니다."),


    /**
     * 3000 : Response 오류
     */
    VALIDATED_ERROR(false, 3000, "VALIDATED_ERROR"), // @Valid 예외 처리
    SEND_MESSAGE_ERROR(false, 3001, "메시지를 발송하는 과정 중 오류가 발생했습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    INTERNAL_SERVER_ERROR(false, 4000, "서버 오류입니다"),
    JSON_PROCESSING_ERROR(false, 4001, "JSON을 처리하는 과정 중 오류가 발생했습니다."),
    FILE_UPLOAD_ERROR(false, 4002, "파일을 업로드 하는 과정 중에 에러가 발생했습니다.");
    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message){
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
