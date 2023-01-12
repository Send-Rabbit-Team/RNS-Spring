package com.srt.message.dto.auth.register.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CredentialResponse {
    String clientId;
    String credential;
    String select_by;
}
