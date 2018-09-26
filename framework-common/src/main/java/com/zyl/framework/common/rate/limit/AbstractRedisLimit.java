package com.zyl.framework.common.rate.limit;

import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

public abstract class AbstractRedisLimit {
    public Long getTime(RedisTemplate redisTemplate) {
        Long currMillSecond = ((StringRedisTemplate)redisTemplate).execute(
                RedisServerCommands::time);
        return currMillSecond;
    }
    abstract Object initToken(Object connection);

    abstract Object acquireToken(Object connection);
}
