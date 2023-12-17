package com.holland.infrastructure.filesystem.disk;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import com.alibaba.fastjson2.JSON;
import com.holland.infrastructure.filesystem.FileDTO;
import com.holland.infrastructure.kit.exception.BizException;
import com.holland.infrastructure.kit.exception.SimpleException;
import com.holland.infrastructure.kit.kit.DateKit;
import com.holland.infrastructure.kit.kit.FileKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository("remoteDiskFileService")
public class RemoteDiskFileServiceImpl implements DiskFileService {
    @Resource
    private RemoteDiskProperties properties;

    @Override
    public FileDTO upload(InputStream inputStream, String specifyDir, String specifyFileName, Map<String, String> meta) {
        if (StrUtil.isEmpty(specifyDir))
            specifyDir = properties.getBase() + File.separatorChar + properties.getCommDir() + File.separator + DateKit.format(LocalDateTime.now(), DateKit.DTF_YYYYMMDDHH);

        final File localFile;
        try {
            localFile = FileKit.tryNewFile("tmp", specifyFileName, inputStream);
        } catch (BizException e) {
            return FileDTO.error(e);
        }

        try {
            FileMetaKit.write(localFile, meta);

            try (Sftp sftp = JschUtil.createSftp(properties.getHost(), properties.getPort(), properties.getUsername(), properties.getPassword())) {
                final String remoteSpecifyDir = specifyDir.replaceAll(FILE_DIR_SPLIT_REGEX, properties.getSeparator());
                sftp.mkDirs(remoteSpecifyDir);
                sftp.upload(remoteSpecifyDir, localFile);
            }

            return FileDTO.success(properties.getBase(), specifyDir, specifyFileName);
        } catch (Exception e) {
            return FileDTO.error(e);
        } finally {
            FileUtil.del(localFile);
        }
    }

    @Override
    public FileDTO upload(MultipartFile file, String specifyDir, String specifyFileName, Map<String, String> meta) {
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
    public void delete(String path) {
        try (Sftp sftp = JschUtil.createSftp(properties.getHost(), properties.getPort(), properties.getUsername(), properties.getPassword())) {
            sftp.delFile(path);
        }
    }

    @Override
    public void delete(FileDTO fileDTO) {
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
    public String preview(String path) {
        return path;
    }

    @Override
    public byte[] downloadFile(String path) {
        try (Sftp sftp = JschUtil.createSftp(properties.getHost(), properties.getPort(), properties.getUsername(), properties.getPassword());
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            sftp.download(path, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new BizException("下载文件失败", e);
        }
    }

    @Override
    public byte[] downloadFile(FileDTO fileDTO) {
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
