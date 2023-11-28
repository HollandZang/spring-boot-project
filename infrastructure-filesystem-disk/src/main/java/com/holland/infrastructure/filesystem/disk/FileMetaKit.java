package com.holland.infrastructure.filesystem.disk;

import com.holland.infrastructure.kit.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class FileMetaKit {
    public static void write(@NonNull File file, @Nullable Map<String, String> meta) {
        if (!CollectionUtils.isEmpty(meta)) {
            final UserDefinedFileAttributeView view = Files.getFileAttributeView(file.toPath(), UserDefinedFileAttributeView.class);
            meta.forEach((k, v) -> {
                try {
                    view.write(k, UTF_8.encode(v));
                } catch (IOException e) {
                    log.warn("写入元数据信息异常：文件[{}], 元数据key[{}], 元数据val[{}]", file.getAbsolutePath(), k, v, e);
                }
            });
        }
    }

    public static String read(@NonNull File file, @NonNull String k) {
        final UserDefinedFileAttributeView userView = Files.getFileAttributeView(file.toPath(), UserDefinedFileAttributeView.class);

        try {
            final int size = userView.size(k);
            final byte[] metadataValue = new byte[size];
            userView.read(k, ByteBuffer.wrap(metadataValue));
            return new String(metadataValue, UTF_8);
        } catch (IOException e) {
            throw new BizException("获取元数据信息异常：文件[{}], 元数据key[{}]", file.getAbsolutePath(), k, e);
        }
    }
}
