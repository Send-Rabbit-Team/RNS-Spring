package com.srt.message.config.response;

import lombok.Getter;

@Getter
public class ErrorResponse {
    String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
