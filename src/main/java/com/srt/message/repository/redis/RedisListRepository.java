package com.srt.message.repository.redis;

import java.util.Collection;

public interface RedisListRepository {
    public void rightPush(Object key, Object value);

    public void rightPushAll(Object key, Collection values);

    public String leftPop(Object key);
}
