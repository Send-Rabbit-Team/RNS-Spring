package com.srt.message.repository.redis;

import com.srt.message.domain.redis.RMessageResult;

import java.util.List;
import java.util.Map;

public interface RMessageResultRepository {
    void save(String key, String rMessageResultId, RMessageResult rMessageResult);

    void saveAll(String key, Map<String, String> rMessageResultMap);

    Map<String, String> findAll(String key);

    String findById(String key, String rMessageResultId);

    void update(String key, String rMessageResultId, RMessageResult rMessageResult);
}
