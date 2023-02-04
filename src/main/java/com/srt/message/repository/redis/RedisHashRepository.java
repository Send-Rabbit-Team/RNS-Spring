package com.srt.message.repository.redis;


import java.util.Map;

public interface RedisHashRepository<RMessageResult> {
    void save(String key, String rMessageResultId, RMessageResult rMessageResult);

    void saveAll(String key, Map<String, String> rMessageResultMap);

    boolean isExist(String key, String rMessageResultId);

    Map<String, String> findAll(String key);

    String findById(String key, String rMessageResultId);

    void update(String key, String rMessageResultId, RMessageResult rMessageResult);
}
