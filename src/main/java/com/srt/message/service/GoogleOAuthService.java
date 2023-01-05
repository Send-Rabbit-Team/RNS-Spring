package com.srt.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.response.BaseResponseStatus;
import com.srt.message.dto.auth.login.post.PostLoginRes;
import com.srt.message.dto.auth.register.google.GoogleOAuthTokenDTO;
import com.srt.message.dto.auth.register.google.GoogleUserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.srt.message.config.response.BaseResponseStatus.JSON_PROCESSING_ERROR;
import static java.util.stream.Collectors.joining;

@Log4j2
@Component
@RequiredArgsConstructor
public class GoogleOAuthService {
    private final String GOOGLE_OAUTH_REDIRECT_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private final String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
    private final String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final AuthService authService;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.scope}")
    private String scope;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    public String getOauthRedirectURL() {
        Map<String, String> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("scope", scope);
        params.put("redirect_uri", redirectUri);

        return params.keySet().stream()
                .map(key -> {
                    try {
                        return key + "=" + encodeValue(params.get(key));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(joining("&", GOOGLE_OAUTH_REDIRECT_URL + "?", ""));
    }

    public Object getGoogleRedirectURL(String code){
        GoogleUserInfoDTO googleUserInfoDTO;

        try {
            ResponseEntity<String> accessTokenResponse = requestAccessToken(code);
            GoogleOAuthTokenDTO oAuthToken = getAccessToken(accessTokenResponse);
            ResponseEntity<String> userInfoResponse = requestUserInfo(oAuthToken);
            googleUserInfoDTO = getUserInfo(userInfoResponse);

            String email = googleUserInfoDTO.getEmail();
            if(authService.checkExistGoogleEmail(email)){
                PostLoginRes postLoginRes = authService.googleSignIn(email);
                return postLoginRes;
            }else{
                return googleUserInfoDTO;
            }
        }catch (JsonProcessingException e){
            throw new BaseException(JSON_PROCESSING_ERROR);
        }
    }

    private ResponseEntity<String> requestAccessToken(String code) {
        log.info(code);
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL, params, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity;
        }
        return null;
    }

    private GoogleOAuthTokenDTO getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        GoogleOAuthTokenDTO googleOAuthTokenDTO = objectMapper.readValue(response.getBody(), GoogleOAuthTokenDTO.class);
        log.info(googleOAuthTokenDTO);
        return googleOAuthTokenDTO;
    }

    private ResponseEntity<String> requestUserInfo(GoogleOAuthTokenDTO oAuthTokenDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthTokenDTO.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
        return response;
    }

    private GoogleUserInfoDTO getUserInfo(ResponseEntity<String> response) throws JsonProcessingException {
        GoogleUserInfoDTO googleUserInfoDTO = objectMapper.readValue(response.getBody(), GoogleUserInfoDTO.class);
        log.info(googleUserInfoDTO);
        return googleUserInfoDTO;

    }
}
