package com.srt.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.AuthPhoneNumberStatus;
import com.srt.message.domain.redis.AuthPhoneNumber;
import com.srt.message.dto.sms.MessageDTO;
import com.srt.message.dto.sms.PhoneNumberValidDTO;
import com.srt.message.dto.sms.SmsRequestDTO;
import com.srt.message.dto.sms.SmsResponseDTO;
import com.srt.message.repository.redis.AuthPhoneNumberRedisRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.ALREADY_AUTH_PHONE_NUMBER;
import static com.srt.message.config.response.BaseResponseStatus.INVALID_AUTH_TOKEN;

@RequiredArgsConstructor
@Service
public class SmsService {

    @Value("${cloud.naver.access_key}")
    private String accessKey;

    @Value("${cloud.naver.secret_key}")
    private String secretKey;

    @Value("${cloud.naver.service_id}")
    private String serviceId;

    @Value("${cloud.naver.sender_phone}")
    private String senderPhoneNumber;

    private final AuthPhoneNumberRedisRepository authPhoneNumberRedisRepository;

    private final long EXPIRATION_SECOND = 60 * 5;

    // 인증 키 생성
    public String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/"+ this.serviceId+"/messages";
        String timestamp = time.toString();
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    // 메시지 전송
    public SmsResponseDTO sendSms(MessageDTO messageDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        String authToken = createAuthToken(messageDto.getTo());
        messageDto.setContent("[Rabbit Notification Service] 인증번호 " + "[" + authToken + "] 를 입력해주세요.");

        List<MessageDTO> messages = new ArrayList<>();
        messages.add(messageDto);

        SmsRequestDTO request = SmsRequestDTO.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(senderPhoneNumber)
                .content(messageDto.getContent())
                .messages(messages)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SmsResponseDTO response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"), httpBody, SmsResponseDTO.class);

        return response;
    }

    // 인증번호 레디스에 저장 (유효기간 5분)
    public String createAuthToken(String phoneNumber){
        AuthPhoneNumber authPhoneNumber = AuthPhoneNumber.builder()
                .phoneNumber(phoneNumber)
                .expiration(EXPIRATION_SECOND)
                .build();

        // 6자리 인증번호 생성
        authPhoneNumber.createAuthToken();

        authPhoneNumber = authPhoneNumberRedisRepository.save(authPhoneNumber);

        return authPhoneNumber.getAuthToken();
    }

    // 인증번호 검증
    public void validAuthToken(PhoneNumberValidDTO phoneNumberValidDTO){
        String phoneNumber = phoneNumberValidDTO.getPhoneNumber();
        String authToken = phoneNumberValidDTO.getAuthToken();

        AuthPhoneNumber authPhoneNumber = authPhoneNumberRedisRepository.findById(phoneNumber)
                .orElseThrow(() -> new BaseException(INVALID_AUTH_TOKEN));

        if(!authPhoneNumber.getAuthToken().equals(authToken))
            throw new BaseException(INVALID_AUTH_TOKEN);

        if(authPhoneNumber.getAuthPhoneNumberStatus() == AuthPhoneNumberStatus.CONFIRM)
            throw new BaseException(ALREADY_AUTH_PHONE_NUMBER);

        authPhoneNumber.changePhoneAuthStatusToConfirm();
        authPhoneNumberRedisRepository.save(authPhoneNumber);
    }
}
