package com.srt.message.service.dto.auth.register.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GoogleUserInfoDTO {
    private String email;
    private Boolean email_verified;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
}
