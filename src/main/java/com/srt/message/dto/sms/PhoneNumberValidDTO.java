package com.srt.message.dto.sms;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class PhoneNumberValidDTO {
    String phoneNumber;
    String authToken;
}
