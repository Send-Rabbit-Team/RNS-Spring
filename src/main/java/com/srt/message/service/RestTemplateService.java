package com.srt.message.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

/**
 * RestTemplate의 Wrapper class
 * exchange를 사용해서 구현
 */
@RequiredArgsConstructor
@Service
public class RestTemplateService<T> {
    private final RestTemplate restTemplate;

    public ResponseEntity<T> get(String url, HttpHeaders httpHeaders) {
        return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, (Class<T>)Object.class);
    }

    public ResponseEntity<T> get(String url, HttpHeaders httpHeaders, Class<T> clazz) {
        return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, clazz);
    }

    public ResponseEntity<T> post(String url, HttpHeaders httpHeaders, Object body) {
        restTemplate.getInterceptors().add((request, body_, execution) -> {
            ClientHttpResponse response = execution.execute(request,body_);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });

        return callApiEndpoint(url, HttpMethod.POST, httpHeaders, body,(Class<T>)Object.class);
    }

    public ResponseEntity<T> post(String url, HttpHeaders httpHeaders, Object body, Class<T> clazz) {
        return callApiEndpoint(url, HttpMethod.POST, httpHeaders, body, clazz);
    }

    // 파일 업로드 API
    public ResponseEntity<T> uploadFile(String url, HttpHeaders headers, MultipartFile multipartFile, Class<T> clazz) {
        Resource body = multipartFile.getResource();

        return callApiEndpoint(url, HttpMethod.PUT, headers, body, clazz);
    }

    private ResponseEntity<T> callApiEndpoint(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, Object body, Class<T> clazz) {
        return restTemplate.exchange(url, httpMethod, new HttpEntity<>(body, httpHeaders), clazz);
    }
}