package com.holland.infrastructure.filesystem.disk;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "holland.filesystem.disk.remote")
public class RemoteDiskProperties {
    private String host;
    private Integer port = 22;
    private String username;
    private String password;

    /**
     * 基础路径
     */
    private String base = "output";
    /**
     * 通用文件存放目录
     */
    private String commDir = "uploadFile";
    /**
     * 远程操作系统文件分隔符
     */
    private String separator = "/";
}
