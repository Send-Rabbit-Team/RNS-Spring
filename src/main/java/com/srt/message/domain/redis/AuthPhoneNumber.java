package com.srt.message.domain.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

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

    public void createAuthToken(){
        this.authToken = RandomStringUtils.randomNumeric(6);
    }
}
