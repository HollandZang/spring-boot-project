package com.holland.infrastructure.filesystem.disk;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.holland.infrastructure.filesystem.FileDTO;
import com.holland.infrastructure.kit.kit.DateKit;
import com.holland.infrastructure.kit.kit.FileKit;
import com.holland.infrastructure.kit.exception.AssertKit;
import com.holland.infrastructure.kit.exception.BizException;
import com.holland.infrastructure.kit.exception.SimpleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository("localDiskFileService")
public class LocalDiskFileServiceImpl implements DiskFileService {

    @Resource
    private LocalDiskProperties properties;

    @Override
    @NonNull
    public FileDTO upload(@NonNull InputStream inputStream, @Nullable String specifyDir, @NonNull String specifyFileName, @Nullable Map<String, String> meta) {
        AssertKit.isNotEmpty(specifyFileName, "文件名不能为空");
        if (StrUtil.isEmpty(specifyDir))
            specifyDir = properties.getBase() + File.separatorChar + properties.getCommDir() + File.separator + DateKit.format(LocalDateTime.now(), DateKit.DTF_YYYYMMDDHH);

        final File file;
        try {
            file = FileKit.tryNewFile(specifyDir, specifyFileName, inputStream);
        } catch (BizException e) {
            return FileDTO.error(e);
        }

        FileMetaKit.write(file, meta);

        return FileDTO.success(properties.getBase(), specifyDir, specifyFileName);
    }

    @Override
    @NonNull
    public FileDTO upload(@NonNull MultipartFile file, @Nullable String specifyDir, @Nullable String specifyFileName, @Nullable Map<String, String> meta) {
        final String originalFilename = file.getOriginalFilename();

        if (StrUtil.isEmpty(specifyFileName))
            specifyFileName = RandomUtil.randomString(32) + '.' + FileUtil.extName(originalFilename);

        if (CollectionUtils.isEmpty(meta)) {
            meta = new HashMap<>(4);
            meta.put("name", file.getName());
            meta.put("originalFilename", originalFilename);
        }

        try (InputStream inputStream = file.getInputStream()) {
            return upload(inputStream, specifyDir, specifyFileName, meta);
        } catch (Exception e) {
            return FileDTO.error(e);
        }
    }

    @Override
    public void delete(@NonNull String path) {
        FileUtil.del(path);
    }

    @Override
    public void delete(@NonNull FileDTO fileDTO) {
        final String relative = fileDTO.getRelative();
        if (StrUtil.isNotEmpty(relative)) {
            delete(relative);
            return;
        }
        final String absolute = fileDTO.getAbsolute();
        if (StrUtil.isNotEmpty(absolute)) {
            delete(absolute);
            return;
        }
        log.warn("待删除文件未提供文件地址：{}", JSON.toJSONString(fileDTO));
    }

    @Override
    @NonNull
    public String preview(@NonNull String path) {
        return path;
    }

    @Override
    @NonNull
    public byte[] downloadFile(@NonNull String path) {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            return IoUtil.readBytes(fileInputStream);
        } catch (IOException e) {
            throw new BizException("下载文件失败", e);
        }
    }

    @Override
    @NonNull
    public byte[] downloadFile(@NonNull FileDTO fileDTO) {
        final String relative = fileDTO.getRelative();
        if (StrUtil.isNotEmpty(relative)) {
            return downloadFile(relative);
        }
        final String absolute = fileDTO.getAbsolute();
        if (StrUtil.isNotEmpty(absolute)) {
            return downloadFile(absolute);
        }
        throw new SimpleException("待下载文件未提供文件地址：{}", JSON.toJSONString(fileDTO));
    }
}
