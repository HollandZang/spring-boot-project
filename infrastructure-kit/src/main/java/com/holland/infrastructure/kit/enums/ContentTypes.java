package com.holland.infrastructure.kit.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
@AllArgsConstructor
public enum ContentTypes {
    GIF("动图", "image/gif", new String[]{"gif"}),
    JPEG("图片", "image/jpeg", new String[]{"jpeg", "jpg", "fif", "jpe"}),
    PNG("无背景图片", "image/png", new String[]{"png"}),
    MP4("视频", "video/mp4", new String[]{"mp4", "flv", "avi", "mov", "rm", "webm", "ts", "rm", "mkv", "mpeg", "ogg", "mpg", "rmvb", "wmv", "3gp", "ts", "swf", "dcm"}),
    ;

    public final String desc;
    public final String val;
    public final String[] extension;

    public static ContentTypes fromExtension(String extension) {
        if (StrUtil.isEmpty(extension)) return null;

        extension = extension.toLowerCase();
        for (ContentTypes anEnum : ContentTypes.values()) {
            for (String anExtension : anEnum.extension) {
                if (anExtension.equals(extension)) return anEnum;
            }
        }
        return null;
    }

    public static boolean isImg(String extension) {
        final ContentTypes contentTypes = fromExtension(extension);
        return GIF.equals(contentTypes) || JPEG.equals(contentTypes) || PNG.equals(contentTypes);
    }
}
