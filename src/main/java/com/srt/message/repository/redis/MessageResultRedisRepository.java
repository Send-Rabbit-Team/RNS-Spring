package com.srt.message.repository.redis;

import com.srt.message.domain.redis.RMessageResult;
import org.springframework.data.repository.CrudRepository;

public interface MessageResultRedisRepository extends CrudRepository<RMessageResult, String> {

}
