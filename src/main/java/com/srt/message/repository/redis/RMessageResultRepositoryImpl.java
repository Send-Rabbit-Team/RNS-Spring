package com.srt.message.repository.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.domain.redis.RMessageResult;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class RMessageResultRepositoryImpl implements RMessageResultRepository{
    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;

    private HashOperations hashOperations;

    public RMessageResultRepositoryImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper){
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(String key, String rMessageResultId, RMessageResult rMessageResult) {
        hashOperations.put(key, rMessageResultId, convertToJson(rMessageResult));
        redisTemplate.expire(key, 60 * 5, TimeUnit.SECONDS);
    }

    @Override
    public void saveAll(String key, Map<String, String> rMessageResultMap) {
        hashOperations.putAll(key, rMessageResultMap);
        redisTemplate.expire(key, 60 * 5, TimeUnit.SECONDS);
    }

    @Override
    public boolean isExist(String key, String rMessageResultId) {
        return hashOperations.hasKey(key, rMessageResultId);
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

    public String convertToJson(Object object){
        String sendMessageJson = null;
        try {
            sendMessageJson = objectMapper.writeValueAsString(object);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return sendMessageJson;
    }
}
