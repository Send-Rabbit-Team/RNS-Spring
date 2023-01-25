package com.srt.message.repository.redis;

import com.srt.message.domain.redis.RMessageResult;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class RMessageResultRepositoryImpl implements RMessageResultRepository{
    private RedisTemplate<String, Object> redisTemplate;

    private HashOperations hashOperations;

    public RMessageResultRepositoryImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(String key, String rMessageResultId, RMessageResult rMessageResult) {
        hashOperations.put(key, rMessageResultId, rMessageResult);
        redisTemplate.expire(key, 60 * 5, TimeUnit.SECONDS);
    }

    @Override
    public void saveAll(String key, Map<String, String> rMessageResultMap) {
        hashOperations.putAll(key, rMessageResultMap);
        redisTemplate.expire(key, 60 * 5, TimeUnit.SECONDS);
    }

    @Override
    public Map<String, String> findAll(String key) {
        return hashOperations.entries(key);
    }

    @Override
    public String findById(String key, String rMessageResultId) {
        return (String) hashOperations.get(key, rMessageResultId);
    }

    @Override
    public void update(String key, String rMessageResultId, RMessageResult rMessageResult) {
        save(key, rMessageResultId, rMessageResult);
        redisTemplate.expire(key, 60 * 5, TimeUnit.SECONDS);
    }
}
