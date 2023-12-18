package com.holland.infrastructure.cache.engine;

import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.lang.NonNull;

public class MyCaffeineCacheManager extends CaffeineCacheManager {
    @NonNull
    @Override
    protected Cache adaptCaffeineCache(@NonNull String name, @NonNull com.github.benmanes.caffeine.cache.Cache<Object, Object> cache) {
        return new MyCaffeineCache(name, cache, isAllowNullValues());
    }
}
