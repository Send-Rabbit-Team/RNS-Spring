package com.srt.message.repository.redis;

import com.srt.message.domain.redis.AuthPhoneNumber;
import org.springframework.data.repository.CrudRepository;


public interface AuthPhoneNumberRedisRepository extends CrudRepository<AuthPhoneNumber, String> {

}
