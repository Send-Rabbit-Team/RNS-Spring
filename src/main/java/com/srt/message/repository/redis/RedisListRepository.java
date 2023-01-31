package com.srt.message.repository.redis;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface RedisListRepository {
    public void rightPush(String key, Object value);

    public void rightPushAll(String key, Collection values, int duration);

    public String leftPop(String key);
}
