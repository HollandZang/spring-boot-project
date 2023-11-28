package com.holland.infrastructure.filesystem.minio;

import lombok.AllArgsConstructor;

/**
 * Minio存储桶分类
 *
 * @see <a href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucketnamingrules.html">桶命名规则</a>
 */
@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
@AllArgsConstructor
public enum MinIOBuckets {
    COMM("通用", "comm"),
//    RESOURCE("统一资源文件管理", "resource"),
//    USER_IMG("用户画像", "user-img"),
//    TMP("临时存储桶", "tmp"),
    ;

    public final String desc;
    public final String name;

    public static MinIOBuckets find(String groupName) {
        if (null == groupName || groupName.isEmpty()) return null;
        groupName = groupName.toLowerCase();

        for (MinIOBuckets bucket : MinIOBuckets.values()) {
            if (bucket.name().equals(groupName) || bucket.name.equals(groupName))
                return bucket;
        }
        return null;
    }
}
