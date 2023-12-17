package com.holland.infrastructure.filesystem;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 文件上传接口
 */
@Validated
public interface FileService {
    /**
     * 当前文件系统默认分隔符的正则表达式
     */
    String FILE_DIR_SPLIT_REGEX = Pattern.quote(File.separator);

    /**
     * 缩略图前缀
     *
     * <p>eg: 如果原图地址是：http://192.168.31.243:9000/user-img/20230423/2d82924c3f734d35b91fe6b15905cd0c.jpeg</p>
     * <p>那么缩略图地址应该是：http://192.168.31.243:9000/user-img/20230423/thumbnail_2d82924c3f734d35b91fe6b15905cd0c.jpeg</p>
     */
    @SuppressWarnings("JavadocLinkAsPlainText")
    String THUMBNAIL_PREFIX = "thumbnail_";

    /**
     * 上传文件
     *
     * @param inputStream     文件流
     * @param specifyDir      是否指定目录，为空则放在通用目录下
     * @param specifyFileName 指定文件名，必填
     * @param meta            元数据信息
     * @return 完整访问路径
     * @apiNote 重名文件会向windows系统一样加上(x)，测试.txt -> 测试(1).txt -> 测试(2).txt
     */
    @NotNull
    FileDTO upload(@NotNull InputStream inputStream, String specifyDir, @NotEmpty String specifyFileName, Map<String, String> meta);

    /**
     * 上传文件
     *
     * @param file            文件
     * @param specifyDir      是否指定目录，为空则放在通用目录下
     * @param specifyFileName 是否指定文件名，为空则随机文件名
     * @param meta            元数据信息
     * @return 完整访问路径
     * @apiNote 重名文件会向windows系统一样加上(x)，测试.txt -> 测试(1).txt -> 测试(2).txt
     */
    @NotNull
    FileDTO upload(@NotNull MultipartFile file, String specifyDir, String specifyFileName, Map<String, String> meta);

    /**
     * 删除文件
     *
     * @param path 文件相对路径或绝对路径
     */
    void delete(@NotEmpty String path);

    /**
     * 删除文件
     *
     * @param fileDTO 文件信息
     */
    void delete(@NotNull FileDTO fileDTO);

    /**
     * 获取预览url
     *
     * @param path 文件相对路径或绝对路径
     * @return 预览地址
     */
    @NotNull
    String preview(@NotEmpty String path);

    /**
     * 下载文件
     *
     * @param path 文件相对路径或绝对路径
     * @return 文件流
     */
    @NotNull
    byte[] downloadFile(@NotEmpty String path);

    /**
     * 下载文件
     *
     * @param fileDTO 文件信息
     * @return 文件流
     */
    @NotNull
    byte[] downloadFile(@NotNull FileDTO fileDTO);
}
