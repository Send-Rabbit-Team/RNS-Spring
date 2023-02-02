package com.srt.message.repository.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisListRepositoryImpl implements RedisListRepository{
    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;
    private ListOperations listOperations;


    public RedisListRepositoryImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.listOperations = redisTemplate.opsForList();
    }

    @Override
    public void rightPush(String key, Object value) {
         listOperations.rightPush(key, value);
        redisTemplate.expire(key, 60 * 5, TimeUnit.SECONDS);
    }

    @Override
    public void rightPushAll(String key, Collection values, int duration) {
        listOperations.rightPushAll(key, values);
        redisTemplate.expire(key, duration, TimeUnit.SECONDS);
    }

    @Override
    public String leftPop(String key) {
        return (String) listOperations.leftPop(key);
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }
}
