package com.srt.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.dto.auth.register.google.CredentialResponse;
import com.srt.message.dto.auth.register.google.GoogleUserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Log4j2
@Component
@RequiredArgsConstructor
public class GoogleOAuthService {
    private final String GOOGLE_USERINFO_REQUEST_URL = "https://oauth2.googleapis.com/tokeninfo";
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public GoogleUserInfoDTO getUserInfo(CredentialResponse credentialResponse) throws JsonProcessingException {
        String url = GOOGLE_USERINFO_REQUEST_URL + "?id_token=" + credentialResponse.getCredential();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        GoogleUserInfoDTO googleUserInfoDTO = objectMapper.readValue(response.getBody(), GoogleUserInfoDTO.class);
        return googleUserInfoDTO;
    }
}
