package com.holland.infrastructure.filesystem.minio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "holland.filesystem.minio")
public class MinIOProperties {
    /**
     * 接口地址
     */
    private String endpoint;
    /**
     * 访问账号
     */
    private String accessKey;
    /**
     * 访问密码
     */
    private String secretKey;
}
