package com.holland.infrastructure.filesystem;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.Serializable;

@Setter
@Accessors(chain = true)
public class FileDTO implements Serializable {
    private FileDTO() {
    }

    /**
     * 保存是否成功
     */
    private Boolean success;
    /**
     * 保存失败原因
     */
    @Getter
    private Exception exception;

    /**
     * 文件名
     */
    @Getter
    private String name;
    /**
     * 文件的相对路径：相对路径+文件名
     */
    @Getter
    private String relative;
    /**
     * 文件访问地址：文件系统访问地址 + 分隔符 + 文件的相对路径
     */
    @Getter
    private String absolute;

    public static FileDTO success(String absolutePathBase, String dir, String name) {
        final String relative = dir + File.separator + name;

        return new FileDTO()
                .setName(name)
                .setRelative(relative)
                .setAbsolute(absolutePathBase.endsWith(File.separator)
                        ? absolutePathBase + relative
                        : absolutePathBase + File.separator + relative)
                ;
    }

    public static FileDTO error(Exception e) {
        return new FileDTO()
                .setSuccess(false)
                .setException(e)
                ;
    }
}