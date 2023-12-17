package com.holland.infrastructure.filesystem.minio;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.holland.infrastructure.filesystem.FileDTO;
import com.holland.infrastructure.kit.enums.ContentTypes;
import com.holland.infrastructure.kit.exception.BizException;
import com.holland.infrastructure.kit.exception.SimpleException;
import com.holland.infrastructure.kit.kit.DateKit;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository("minIOFileService")
public class MinIOFileServiceImpl implements MinIOFileService {
    @Resource
    private MinioClient minioClient;
    @Resource
    private MinIOProperties properties;
    @Resource
    private List<String> cacheBuckets;

    private void checkBucket(String bucket) throws Exception {
        if (!cacheBuckets.contains(bucket)) {
            MinIOPolicy.toPublic(minioClient, bucket);
            cacheBuckets.add(bucket);
        }
    }

    @Override
    public FileDTO upload(InputStream inputStream, String specifyDir, String specifyFileName, Map<String, String> meta) {
        if (StrUtil.isEmpty(specifyDir))
            specifyDir = MinIOBuckets.COMM.name + File.separator + DateKit.format(LocalDateTime.now(), DateKit.DTF_YYYYMMDDHH);

        final String[] dirs = specifyDir.split(FILE_DIR_SPLIT_REGEX);

        final String bucket = dirs[0];
        try {
            checkBucket(bucket);
        } catch (Exception e) {
            return FileDTO.error(e);
        }

        final String filePath = Arrays.stream(dirs).skip(1).collect(Collectors.joining(File.separator));

        try {
            final PutObjectArgs.Builder builder = PutObjectArgs.builder();

            final ContentTypes contentTypes = ContentTypes.fromExtension(FileUtil.extName(specifyFileName));
            if (null != contentTypes) builder.contentType(contentTypes.val);

            builder.bucket(bucket).object(StrUtil.isEmpty(filePath) ? specifyFileName : filePath + '/' + specifyFileName).stream(inputStream, inputStream.available(), -1).userMetadata(meta);

            minioClient.putObject(builder.build());

            return FileDTO.success(properties.getEndpoint(), specifyDir, specifyFileName);
        } catch (Exception e) {
            return FileDTO.error(e);
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
        final String[] dirs = path.split(FILE_DIR_SPLIT_REGEX);
        final String bucket = dirs[0];
        final String filePath = Arrays.stream(dirs).skip(1).collect(Collectors.joining(File.separator));

        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(filePath).build());
        } catch (Exception e) {
            log.error("删除文件异常：{}", path, e);
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
            delete(absolute.substring(properties.getEndpoint().length()));
            return;
        }
        log.warn("待删除文件未提供文件地址：{}", JSON.toJSONString(fileDTO));
    }

    @Override
    public String preview(String path) {
        final String[] dirs = path.split(FILE_DIR_SPLIT_REGEX);
        final String bucket = dirs[0];
        final String filePath = Arrays.stream(dirs).skip(1).collect(Collectors.joining(File.separator));

        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucket).object(filePath).method(Method.GET).build());
        } catch (Exception e) {
            throw new BizException("获取文件预览地址失败", e);
        }
    }

    @Override
    public byte[] downloadFile(String path) {
        final String[] dirs = path.split(FILE_DIR_SPLIT_REGEX);
        final String bucket = dirs[0];
        final String filePath = Arrays.stream(dirs).skip(1).collect(Collectors.joining(File.separator));
        try {
            GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .build());
            return IoUtil.readBytes(response);
        } catch (Exception e) {
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
            return downloadFile(absolute.substring(properties.getEndpoint().length()));
        }
        throw new SimpleException("待下载文件未提供文件地址：{}", JSON.toJSONString(fileDTO));
    }
}
