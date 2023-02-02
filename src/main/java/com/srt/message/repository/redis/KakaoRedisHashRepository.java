package com.srt.message.repository.redis;

import com.srt.message.domain.redis.RKakaoMessageResult;
import com.srt.message.domain.redis.RMessageResult;

import java.util.Map;

public interface KakaoRedisHashRepository {
    void save(String key, String rMessageResultId, RKakaoMessageResult rMessageResult);

    void saveAll(String key, Map<String, String> rMessageResultMap);

    boolean isExist(String key, String rMessageResultId);

    Map<String, String> findAll(String key);

    String findById(String key, String rMessageResultId);

    void update(String key, String rMessageResultId, RKakaoMessageResult rMessageResult);
}
