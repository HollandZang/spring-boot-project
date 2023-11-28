package com.holland.infrastructure.filesystem.disk;

import com.holland.infrastructure.filesystem.FileService;

/**
 * 本地/远程磁盘 文件上传接口
 *
 * @apiNote 一些不支持文件元数据的文件系统包括: FAT, exFAT, ISO 9660, UDF, NFS, etc...
 */
public interface DiskFileService extends FileService {
}
