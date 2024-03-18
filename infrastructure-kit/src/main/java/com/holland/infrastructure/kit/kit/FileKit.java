package com.holland.infrastructure.kit.kit;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.holland.infrastructure.kit.exception.BizException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileKit {
    private FileKit() {
    }

    /**
     * 创建一个新的文件，如果存在就直接返回
     *
     * @param path     文件地址
     * @param filename 文件名
     * @return 文件对象
     */
    public static File tryNewFile(String path, String filename) throws BizException {
        mkdirs(path);
        String uri = StrUtil.isEmpty(path) ? filename : path + File.separatorChar + filename;
        File file = new File(uri);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new BizException("创建文件失败", e);
            }
        }
        return file;
    }

    public static File tryNewFile(String path, String filename, InputStream content) throws BizException {
        final File file = tryNewFile(path, filename);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(IoUtil.readBytes(content));
        } catch (IOException e) {
            throw new BizException(e);
        }
        return file;
    }

    public static File mkdirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static String read2Str(File file) throws BizException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return IoUtil.read(fileInputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BizException("读取文件失败：" + file.getAbsolutePath(), e);
        }
    }

    public static <T> T read2Obj(File file, Class<T> tClass) throws BizException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = IoUtil.readBytes(fileInputStream);
            return JSON.parseObject(bytes, tClass);
        } catch (IOException e) {
            throw new BizException("读取文件失败：" + file.getAbsolutePath(), e);
        }
    }

    public static <T> T read2Obj(String path, Class<T> tClass) throws BizException {
        return read2Obj(new File(path), tClass);
    }

    public static <T> T read2Obj(String path, String name, Class<T> tClass) throws BizException {
        return read2Obj(path + File.separatorChar + name, tClass);
    }
}
