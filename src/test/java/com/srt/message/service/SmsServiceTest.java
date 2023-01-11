package com.srt.message.service;

import com.srt.message.domain.redis.AuthPhoneNumber;
import com.srt.message.dto.sms.PhoneNumberValidDTO;
import com.srt.message.repository.redis.AuthPhoneNumberRedisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SmsServiceTest {
    @Autowired
    private SmsService smsService;

    @Autowired
    private AuthPhoneNumberRedisRepository authPhoneNumberRedisRepository;

    @Test
    public void validAuthToken_equalAuthToken_True(){
        // given
        AuthPhoneNumber authPhoneNumber = AuthPhoneNumber.builder()
                .phoneNumber("01012341234")
                .authToken("123123")
                .expiration(300L)
                .build();

        authPhoneNumberRedisRepository.save(authPhoneNumber);

        // when
        PhoneNumberValidDTO phoneNumberValidDTO = PhoneNumberValidDTO.builder()
                .phoneNumber("01012341234")
                .authToken("123123")
                .build();

        // then -> not exception
        smsService.validAuthToken(phoneNumberValidDTO);

    }
}