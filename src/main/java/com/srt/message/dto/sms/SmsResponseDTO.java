package com.srt.message.dto.sms;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class SmsResponseDTO {
    String requestId;
    LocalDateTime requestTime;
    String statusCode;
    String statusName;
}