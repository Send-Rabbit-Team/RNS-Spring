package com.srt.message.repository.redis;

import com.srt.message.domain.redis.RKakaoMessageResult;
import com.srt.message.domain.redis.RMessageResult;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class RKakaoMessageResultRepositoryImpl implements RKakaoMessageResultRepository{
    private RedisTemplate<String, Object> redisTemplate;

    private HashOperations hashOperations;

    public RKakaoMessageResultRepositoryImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(String key, String rMessageResultId, RKakaoMessageResult rKakaoMessageResult) {
        hashOperations.put(key, rMessageResultId, rKakaoMessageResult);
        redisTemplate.expire(key, 60 * 5, TimeUnit.SECONDS);
    }

    @Override
    public void saveAll(String key, Map<String, String> rKakaoMessageResultMap) {
        hashOperations.putAll(key, rKakaoMessageResultMap);
        redisTemplate.expire(key, 60 * 5, TimeUnit.SECONDS);
    }

    @Override
    public Map<String, String> findAll(String key) {
        return hashOperations.entries(key);
    }

    @Override
    public String findById(String key, String rKakaoMessageResultId) {
        return (String) hashOperations.get(key, rKakaoMessageResultId);
    }

    @Override
    public void update(String key, String rKakaoMessageResultId, RKakaoMessageResult rKakaoMessageResult) {
        save(key, rKakaoMessageResultId, rKakaoMessageResult);
        redisTemplate.expire(key, 60 * 5, TimeUnit.SECONDS);
    }
}
