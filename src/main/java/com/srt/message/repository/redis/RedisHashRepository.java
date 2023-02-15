package com.srt.message.repository.redis;


import java.util.Map;

public interface RedisHashRepository<T> {
    void save(String key, String rMessageResultId, T rMessageResult);

    void saveAll(String key, Map<String, String> rMessageResultMap);

    void saveContactAll(String key, Map<String, String> contactMap);

    boolean isExist(String key, String rMessageResultId);

    Map<String, String> findAll(String key);

    String findByRMessageResultId(String key, String rMessageResultId);

    String findByContactId(String key, String contactId);

    void update(String key, String rMessageResultId, T rMessageResult);

    void delete(String key, Object value);
}
