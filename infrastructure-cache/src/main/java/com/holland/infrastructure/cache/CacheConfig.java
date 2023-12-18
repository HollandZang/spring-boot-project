package com.holland.infrastructure.cache;

import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.holland.infrastructure.cache.engine.MyCaffeineCacheManager;
import com.holland.infrastructure.cache.engine.MyRedisCacheManager;
import com.holland.infrastructure.kit.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@EnableCaching
@Configuration
public class CacheConfig {
    @Resource
    private ApplicationContext ctx;
    @Resource
    private CacheProperties cacheProperties;

    @Bean
    public CacheManager cacheManager() {
        List<CacheManager> cacheManagers = cacheProperties.getSortList().stream()
                .map(cacheParams -> {
                    final CacheProperties.CaffeineParams caffeine = cacheParams.getCaffeine();
                    final CacheProperties.RedisParams redis = cacheParams.getRedis();
                    if (null != caffeine && null != redis) {
                        throw new BizException("单个缓存不可以同时配置多个实现方式: {}", JSON.toJSONString(cacheParams));
                    }

                    if (null != caffeine) {
                        return caffeineCacheManager(cacheParams);
                    }
                    if (null != redis) {
                        return redisCacheManager(cacheParams);
                    }

                    throw new BizException("缓存没有指定实现方式: {}", JSON.toJSONString(cacheParams));
                })
                .collect(Collectors.toList());

        final CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        compositeCacheManager.setCacheManagers(cacheManagers);
        return compositeCacheManager;
    }

    public CaffeineCacheManager caffeineCacheManager(CacheProperties.CacheParams cacheParams) {
        if (log.isInfoEnabled()) {
            log.info("加载 caffeine 缓存: {}", JSON.toJSONString(cacheParams));
        }
        final CacheProperties.CaffeineParams caffeine = cacheParams.getCaffeine();

        final CaffeineCacheManager cacheManager = new MyCaffeineCacheManager();

        // 通用
        final Caffeine<Object, Object> commonCaffeine = Caffeine.newBuilder()
                .initialCapacity(caffeine.getInitialCapacity())
                .maximumSize(caffeine.getMaximumSize())
//                .weakKeys()
//                .recordStats()
                ;
        if (caffeine.isRefreshExpireTime()) {
            commonCaffeine.expireAfterAccess(Duration.ofSeconds(cacheParams.getTtl()));
        } else {
            commonCaffeine.expireAfterWrite(Duration.ofSeconds(cacheParams.getTtl()));
        }
        cacheManager.setCaffeine(commonCaffeine);

        // 指定键
        if (null != caffeine.getSpecKeys()) {
            for (CacheProperties.SpecCaffeineParams specKey : caffeine.getSpecKeys()) {
                final Caffeine<Object, Object> specCaffeine = Caffeine.newBuilder()
                        .initialCapacity(specKey.getInitialCapacity())
                        .maximumSize(specKey.getMaximumSize());
                if (specKey.isRefreshExpireTime()) {
                    specCaffeine.expireAfterAccess(Duration.ofSeconds(specKey.getTtl()));
                } else {
                    specCaffeine.expireAfterWrite(Duration.ofSeconds(specKey.getTtl()));
                }
                cacheManager.registerCustomCache(specKey.getKeyName(), specCaffeine.build());
            }
        }

        return cacheManager;
    }

    public RedisCacheManager redisCacheManager(CacheProperties.CacheParams cacheParams) {
        if (log.isInfoEnabled()) {
            log.info("加载 redis 缓存: {}", JSON.toJSONString(cacheParams));
        }

        final CacheProperties.RedisParams redis = cacheParams.getRedis();

        final RedisConnectionFactory redisConnectionFactory = ctx.getBean(RedisConnectionFactory.class);

        final RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);

        // 通用
        final RedisCacheConfiguration commonRedisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(cacheParams.getTtl()));
        setKeyPrefix(commonRedisCacheConfiguration, redis.getCacheKeyPrefix(), redis.getCacheKeySeparator());

        final RedisCacheManager redisCacheManager;
        if (null == redis.getSpecKeys()) {
            redisCacheManager = new MyRedisCacheManager(redisCacheWriter, commonRedisCacheConfiguration, true);
        } else {
            // 指定键
            final Map<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<>();
            for (CacheProperties.SpecRedisParams specKey : redis.getSpecKeys()) {
                final RedisCacheConfiguration specRedisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofSeconds(specKey.getTtl()));
                setKeyPrefix(specRedisCacheConfiguration, specKey.getCacheKeyPrefix(), specKey.getCacheKeySeparator());

                initialCaches.put(specKey.getKeyName(), specRedisCacheConfiguration);
            }
            redisCacheManager = new MyRedisCacheManager(redisCacheWriter, commonRedisCacheConfiguration, initialCaches, true);
        }

        redisCacheManager.setTransactionAware(redis.isTransactionAware());
        return redisCacheManager;
    }

    private static void setKeyPrefix(RedisCacheConfiguration commonRedisCacheConfiguration, String prefix, String separator) {
        if (StringUtils.hasLength(prefix)) {
            commonRedisCacheConfiguration.computePrefixWith(new CacheKeyPrefix() {
                @NonNull
                @Override
                public String compute(@NonNull String cacheName) {
                    return prefix + separator;
                }
            });
        }
    }
}
