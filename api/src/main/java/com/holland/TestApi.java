package com.holland;

import com.holland.infrastructure.filesystem.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestApi {

    @Resource(name = "remoteDiskFileService")
    private FileService fileService;

    @PostMapping("/save")
    public void save(MultipartFile file) {
        fileService.upload(file, null, null, null);
    }

    @PostMapping("/download")
    public void download(String file) {
        final byte[] bytes = fileService.downloadFile(file);
        final File file1 = new File("tmp.jpg");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file1)) {
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
