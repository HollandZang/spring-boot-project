package com.holland.infrastructure.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class MyCacheResolver extends SimpleCacheResolver {
    @Autowired
    public MyCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    @NonNull
    @Override
    public Collection<? extends Cache> resolveCaches(@NonNull CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = getCacheNames(context);
        if (cacheNames == null) {
            return Collections.emptyList();
        }
        Collection<Cache> result = new ArrayList<>(cacheNames.size());
        for (String cacheName : cacheNames) {
            final CacheManager cacheManager = getCacheManager();

            if (cacheManager instanceof CompositeCacheManager) {
                // 把多个符合的cache放进来
                addCacheFromCompositeCacheManager(result, cacheName, (CompositeCacheManager) cacheManager);
            } else {
                // 常规逻辑
                Cache cache = cacheManager.getCache(cacheName);
                if (cache == null) {
                    throw new IllegalArgumentException("Cannot find cache named '" +
                            cacheName + "' for " + context.getOperation());
                }
                result.add(cache);
            }
        }
        return result;
    }

    private void addCacheFromCompositeCacheManager(Collection<Cache> result, String cacheName, CompositeCacheManager compositeCacheManager) {
        final Field cacheManagersField;
        try {
            cacheManagersField = CompositeCacheManager.class.getDeclaredField("cacheManagers");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        cacheManagersField.setAccessible(true);
        final List<CacheManager> cacheManagers;
        try {
            //noinspection unchecked
            cacheManagers = (List<CacheManager>) cacheManagersField.get(compositeCacheManager);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        for (CacheManager cacheManager : cacheManagers) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                result.add(cache);
            }
        }
    }
}
