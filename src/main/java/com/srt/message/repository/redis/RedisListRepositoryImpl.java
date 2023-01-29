package com.srt.message.repository.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

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
    public void rightPush(Object key, Object value) {
         listOperations.rightPush(key, value);
    }

    @Override
    public void rightPushAll(Object key, Collection values) {
        listOperations.rightPushAll(key, values);
    }

    @Override
    public String leftPop(Object key) {
        return (String) listOperations.leftPop(key);
    }
}
