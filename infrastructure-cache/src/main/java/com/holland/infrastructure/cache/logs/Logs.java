package com.holland.infrastructure.cache.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logs {
    private static final String KEY_PREFIX = "holland.cache.";

    /**
     * 打印返回值来源
     *
     * @apiNote 如果值从缓存获取，会打印出当前缓存与值。eg: Return form caffeine cache -> {value}
     */
    public static final Logger VALUE_FROM = LoggerFactory.getLogger(KEY_PREFIX + "value_from");
}
