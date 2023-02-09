package com.srt.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.redis.AuthPhoneNumber;
import com.srt.message.dto.sms.PhoneNumberValidDTO;
import com.srt.message.repository.redis.AuthPhoneNumberRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.srt.message.config.response.BaseResponseStatus.INVALID_AUTH_TOKEN;
import static com.srt.message.config.response.BaseResponseStatus.NOT_AUTH_PHONE_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmsServiceTest {
    private final long EXPIRATION_SECOND = 60 * 5;

    @InjectMocks
    private SmsService smsService;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private AuthPhoneNumberRedisRepository authPhoneNumberRedisRepository;

    private AuthPhoneNumber authPhoneNumber;

    @BeforeEach
    void setUp(){
        authPhoneNumber = AuthPhoneNumber.builder()
                .phoneNumber("01012341234")
                .expiration(EXPIRATION_SECOND)
                .authToken("123456")
                .build();
    }

    @DisplayName("인증번호 레디스에 저장")
    @Test
    void createAuthToken_Success() {
        // given
        doReturn(authPhoneNumber).when(authPhoneNumberRedisRepository).save(any());

        // when
        String authToken = smsService.createAuthToken("01012341234");

        // then
        assertThat(authToken).isEqualTo(authPhoneNumber.getAuthToken());
    }

    @DisplayName("인증번호 검증 (성공)")
    @Test
    void validAuthToken_Success() {
        // given
        PhoneNumberValidDTO request = PhoneNumberValidDTO.builder()
                .phoneNumber("01012341234").authToken("123456").build();

        doReturn(Optional.ofNullable(authPhoneNumber)).when(authPhoneNumberRedisRepository).findById(any());
        doReturn(null).when(authPhoneNumberRedisRepository).save(any());

        // when
        smsService.validAuthToken(request);

        // verify
        verify(authPhoneNumberRedisRepository, times(1)).save(any());
    }

    @DisplayName("인증번호 검증 (실패 - 인증 번호가 올바르지 않음)")
    @Test
    void validAuthToken_Fail1() {
        // given
        PhoneNumberValidDTO request = PhoneNumberValidDTO.builder()
                .phoneNumber("01012341234").authToken("111111").build();

        doReturn(Optional.ofNullable(authPhoneNumber)).when(authPhoneNumberRedisRepository).findById(any());

        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                smsService.validAuthToken(request));

        // then
        assertEquals(exception.getStatus(), INVALID_AUTH_TOKEN);
    }
}