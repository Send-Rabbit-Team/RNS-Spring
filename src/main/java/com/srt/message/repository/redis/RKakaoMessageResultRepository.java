package com.srt.message.repository.redis;

import com.srt.message.domain.redis.RKakaoMessageResult;
import com.srt.message.domain.redis.RMessageResult;

import java.util.Map;

public interface RKakaoMessageResultRepository {
    void save(String key, String rKakaoMessageResultId, RKakaoMessageResult rKakaoMessageResult);

    void saveAll(String key, Map<String, String> rKakaoMessageResultMap);

    Map<String, String> findAll(String key);

    String findById(String key, String rKakaoMessageResultId);

    void update(String key, String rKakaoMessageResultId, RKakaoMessageResult rKakaoMessageResult);
}
