package com.holland.infrastructure.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "holland.caches")
public class CacheProperties {
    private List<CacheParams> sortList;

    @Getter
    @Setter
    public static class CacheParams {
        /**
         * Time to Live duration
         */
        private int ttl;
        /**
         * 缓存类型选择 caffeine
         */
        private CaffeineParams caffeine;
        /**
         * 缓存类型选择 redis
         */
        private RedisParams redis;
    }

    @Getter
    @Setter
    public static class CaffeineParams {
        /**
         * 初始缓存容量, caffeine 参数
         */
        private int initialCapacity = 16;
        /**
         * 指定缓存可能包含的最大条目数, caffeine 参数
         */
        private int maximumSize = 1024;
        /**
         * 访问之后是否会刷新过期时间，默认不会刷新(expireAfterWrite)，设置 true 会刷新(expireAfterAccess)
         */
        private boolean refreshExpireTime = false;
        /**
         * 设置指定键的缓存
         */
        private List<SpecCaffeineParams> specKeys;
    }

    @Getter
    @Setter
    public static class RedisParams {
        /**
         * 缓存键前缀
         */
        private String cacheKeyPrefix;
        /**
         * 缓存键分隔符
         */
        private String cacheKeySeparator = ":";
        /**
         * 设置该CacheManager是否应该公开事务感知缓存对象。 默认为“false”。将其设置为“true”，将缓存放/放操作与正在进行的spring管理的事务同步，仅在成功事务的提交后阶段执行实际的缓存放/放操作
         */
        private boolean transactionAware = false;
        /**
         * 设置指定键的缓存
         */
        private List<SpecRedisParams> specKeys;
    }

    @Getter
    @Setter
    public static class SpecCaffeineParams extends CaffeineParams {
        /**
         * Time to Live duration
         */
        private int ttl;
        /**
         * 指定缓存键名
         */
        private String keyName;
        /**
         * 初始缓存容量, caffeine 参数
         */
        private int initialCapacity = 16;
        /**
         * 指定缓存可能包含的最大条目数, caffeine 参数
         */
        private int maximumSize = 1024;
        /**
         * 访问之后是否会刷新过期时间，默认不会刷新(expireAfterWrite)，设置 true 会刷新(expireAfterAccess)
         */
        private boolean refreshExpireTime = false;
    }

    @Getter
    @Setter
    public static class SpecRedisParams extends RedisParams {
        /**
         * Time to Live duration
         */
        private int ttl;
        /**
         * 指定缓存键名
         */
        private String keyName;
        /**
         * 缓存键前缀
         */
        private String cacheKeyPrefix;
        /**
         * 缓存键分隔符
         */
        private String cacheKeySeparator = ":";
    }
}
