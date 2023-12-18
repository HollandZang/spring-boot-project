package com.holland.infrastructure.cache.engine;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

public class MyRedisCacheManager extends RedisCacheManager {
    protected final RedisCacheWriter cacheWriter;
    protected final boolean allowInFlightCacheCreation;
    protected final RedisCacheConfiguration defaultCacheConfiguration;
    protected final Map<String, RedisCacheConfiguration> initialCacheConfigurations;

    public MyRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation);
        this.cacheWriter = cacheWriter;
        this.allowInFlightCacheCreation = allowInFlightCacheCreation;
        this.defaultCacheConfiguration = defaultCacheConfiguration;
        this.initialCacheConfigurations = new HashMap<>();
    }

    public MyRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
        this.cacheWriter = cacheWriter;
        this.allowInFlightCacheCreation = allowInFlightCacheCreation;
        this.defaultCacheConfiguration = defaultCacheConfiguration;
        this.initialCacheConfigurations = initialCacheConfigurations;
    }

    @NonNull
    @Override
    protected RedisCache createRedisCache(@NonNull String name, RedisCacheConfiguration cacheConfig) {
        return new MyRedisCache(name, cacheWriter, cacheConfig);
    }

    @Override
    protected RedisCache getMissingCache(String name) {
        return allowInFlightCacheCreation ? createRedisCache(name, initialCacheConfigurations.getOrDefault(name, defaultCacheConfiguration)) : null;
    }
}
