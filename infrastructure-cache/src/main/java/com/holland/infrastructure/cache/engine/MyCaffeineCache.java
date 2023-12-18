package com.holland.infrastructure.cache.engine;

import com.github.benmanes.caffeine.cache.Cache;
import com.holland.infrastructure.cache.logs.Logs;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.lang.NonNull;

public class MyCaffeineCache extends CaffeineCache {
    public MyCaffeineCache(String name, Cache<Object, Object> cache, boolean allowNullValues) {
        super(name, cache, allowNullValues);
    }

    @Override
    protected Object lookup(@NonNull Object key) {
        final Object lookup = super.lookup(key);
        if (null != lookup) {
            Logs.VALUE_FROM.debug("Return form caffeine cache -> {}", lookup);
        }
        return lookup;
    }
}
