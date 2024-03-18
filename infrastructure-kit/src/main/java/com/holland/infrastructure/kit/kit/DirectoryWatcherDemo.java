package com.holland.infrastructure.kit.kit;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DirectoryWatcherDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();

        // 监听太多文件会有性能问题，所以监听文件夹
        Path dir = Paths.get("D:\\project\\spring-boot-project\\infrastructure-kit\\src\\main\\java\\com\\holland\\infrastructure\\kit");
        registerAll(dir, watchService);
        processFilesThatHaveChanged();

        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                Path context = (Path) event.context();
                Path path = ((Path) key.watchable()).resolve(context);
                // 通过工具修改文件（如idea）会生成临时文件，过滤掉
                if (!path.toString().endsWith("~")) {
                    if (Files.isRegularFile(path)) {
                        // 通过工具修改文件（如idea）可能会出现多次文件变更，所以去重
                        changedFiles.put(path.toString(), context);
                    }
                }
            }
            key.reset();
        }
    }

    private static void registerAll(final Path start, final WatchService watchService) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    static ConcurrentHashMap<String, Object> changedFiles = new ConcurrentHashMap<>();

    public static void processFilesThatHaveChanged() {
        executor.scheduleWithFixedDelay(() -> {
            try {
                Set<String> keySet = changedFiles.keySet();
                for (String filePath : keySet) {
                    changedFiles.remove(filePath);
                    final String string = FileKit.read2Str(new File(filePath));
                    System.out.printf("文件[%s]变化，新内容为下：\n%s\n\n", filePath, string);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 500, 500, TimeUnit.MILLISECONDS);

    }
}
