package com.srt.message.repository.redis;

import com.srt.message.domain.redis.AuthPhoneNumber;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthPhoneNumberRedisRepository extends CrudRepository<AuthPhoneNumber, String> {

}
