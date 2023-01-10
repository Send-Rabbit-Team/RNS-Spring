package com.srt.message.domain.redis;

import com.srt.message.config.status.AuthPhoneNumberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@RedisHash("phoneNumber") // Redis Repository 이용하기 위해 선언
public class AuthPhoneNumber {
    @Id
    private String phoneNumber;

    private String authToken;

    @TimeToLive // 유효시간 값 (초 단위)
    private Long expiration;

    @Enumerated(EnumType.STRING)
    private AuthPhoneNumberStatus authPhoneNumberStatus = AuthPhoneNumberStatus.PENDING;

    public void createAuthToken(){
        this.authToken = RandomStringUtils.randomNumeric(6);
    }

    public void changePhoneAuthStatusToConfirm(){
        this.authPhoneNumberStatus = AuthPhoneNumberStatus.CONFIRM;
    }
}
