package com.holland.infrastructure.filesystem.minio;

import com.holland.infrastructure.kit.exception.NotImplementedException;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;

/**
 * MinIO 访问权限控制
 */
public class MinIOPolicy {
    public static void toPublic(MinioClient minioClient, String groupName) throws Exception {
        final String config = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\",\"s3:GetBucketLocation\"],\"Resource\":[\"arn:aws:s3:::" + groupName + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:PutObject\",\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\"],\"Resource\":[\"arn:aws:s3:::" + groupName + "/*\"]}]}";
        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(groupName)
                        .config(config)
                        .build()
        );
    }

    public static void toPublic(MinioClient minioClient, MinIOBuckets groups) throws Exception {
        toPublic(minioClient, groups.name);
    }

    public static void toPrivate(MinioClient minioClient, MinIOBuckets groups) throws Exception {
        final String config = "{\"Statement\":[],\"Version\":\"2012-10-17\"}";
        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(groups.name)
                        .config(config)
                        .build()
        );
    }

    public static void toCustom(MinioClient minioClient, MinIOBuckets groups) {
        throw new NotImplementedException("自定义MinIO访问策略");
    }
}
