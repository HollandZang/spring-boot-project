package com.holland.infrastructure.cache.engine;

import com.holland.infrastructure.cache.logs.Logs;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.NonNull;

public class MyRedisCache extends RedisCache {
    /**
     * Create new {@link RedisCache}.
     *
     * @param name        must not be {@literal null}.
     * @param cacheWriter must not be {@literal null}.
     * @param cacheConfig must not be {@literal null}.
     */
    public MyRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
    }

    @Override
    protected Object lookup(@NonNull Object key) {
        final Object lookup = super.lookup(key);
        if (null != lookup) {
            Logs.VALUE_FROM.debug("Return form redis cache -> {}", lookup);
        }
        return lookup;
    }
}
