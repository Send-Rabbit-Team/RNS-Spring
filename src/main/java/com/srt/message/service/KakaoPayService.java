package com.srt.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.dto.kakao_pay.KakaoPayVO;
import com.srt.message.dto.kakao_pay.PaymentApproveRes;
import com.srt.message.dto.kakao_pay.PaymentReadyRes;
import com.srt.message.dto.point.get.GetPointRes;
import com.srt.message.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static com.srt.message.config.response.BaseResponseStatus.INVALID_KAKAO_PAY;

//@Scope(value = "request")
@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoPayService {
    private final String KAKAO_PAYMENT_READY_URL = "https://kapi.kakao.com/v1/payment/ready";
    private final String KAKAO_PAYMENT_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";
    private final String REDIRECT_URI = "http://localhost:8080/point/redirect";

    private final String TEST_CID = "TC0ONETIME";
    private final String ADMIN_KEY = "7e6a8588cc37131e7467de9d24a7ca8a";
    private final int SMS_PRICE = 10;
    private final int KAKAO_PRICE = 7;

    private final KakaoPayVO kakaoPayVO;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final PointService pointService;

    public String paymentReady(long memberId, int smsPoint, int kakaoPoint) {
        String orderId = String.valueOf(UUID.randomUUID());
        int totalAmount = smsPoint * SMS_PRICE + kakaoPoint * KAKAO_PRICE;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + ADMIN_KEY);
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", TEST_CID);
        params.add("partner_order_id", orderId);
        params.add("partner_user_id", String.valueOf(memberId));
        params.add("item_name", "RNS 충전결제");
        params.add("quantity", "1");
        params.add("total_amount", String.valueOf(totalAmount));
        params.add("tax_free_amount", "0");
        params.add("vat_amount", "0");
        params.add("approval_url", REDIRECT_URI);
        params.add("fail_url", REDIRECT_URI);
        params.add("cancel_url", REDIRECT_URI);

        ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_PAYMENT_READY_URL, new HttpEntity<>(params, headers), String.class);

        try {
            PaymentReadyRes paymentReadyRes = objectMapper.readValue(response.getBody(), PaymentReadyRes.class);

            kakaoPayVO.setTid(paymentReadyRes.getTid());
            kakaoPayVO.setMemberId(memberId);
            kakaoPayVO.setOrderId(orderId);
            kakaoPayVO.setSmsPoint(smsPoint);
            kakaoPayVO.setKakaoPoint(kakaoPoint);
            kakaoPayVO.setTotalAmount(totalAmount);

            return paymentReadyRes.getNext_redirect_pc_url();
        } catch (JsonProcessingException e) {
            throw new BaseException(INVALID_KAKAO_PAY);
        }
    }

    public GetPointRes paymentApprove(String pgToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + ADMIN_KEY);
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", TEST_CID);
        params.add("tid", kakaoPayVO.getTid());
        params.add("partner_order_id", kakaoPayVO.getOrderId());
        params.add("partner_user_id", String.valueOf(kakaoPayVO.getMemberId()));
        params.add("pg_token", pgToken);

        ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_PAYMENT_APPROVE_URL, new HttpEntity<>(params, headers), String.class);

        try {
            PaymentApproveRes paymentApproveRes = objectMapper.readValue(response.getBody(), PaymentApproveRes.class);

            long memberId = Long.parseLong(paymentApproveRes.getPartner_user_id());
            int totalAmount = Integer.parseInt(paymentApproveRes.getAmount().getTotal());

            if (memberId == kakaoPayVO.getMemberId() && totalAmount == kakaoPayVO.getTotalAmount())
                return pointService.chargePoint(memberId, kakaoPayVO.getSmsPoint(), kakaoPayVO.getKakaoPoint());
            else
                throw new BaseException(INVALID_KAKAO_PAY);
        } catch (JsonProcessingException e) {
            throw new BaseException(INVALID_KAKAO_PAY);
        }
    }
}
