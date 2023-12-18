package com.holland.infrastructure.filesystem.disk;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "holland.filesystem.disk.local")
public class LocalDiskProperties {
    /**
     * 基础路径
     */
    private String base = "output";
    /**
     * 通用文件存放目录
     */
    private String commDir = "uploadFile";
}
