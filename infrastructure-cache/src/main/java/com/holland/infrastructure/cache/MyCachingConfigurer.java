package com.holland.infrastructure.cache;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MyCachingConfigurer implements CachingConfigurer {
    @Resource
    public MyCacheResolver myCacheResolver;

    @Override
    public CacheResolver cacheResolver() {
        return myCacheResolver;
    }
}
