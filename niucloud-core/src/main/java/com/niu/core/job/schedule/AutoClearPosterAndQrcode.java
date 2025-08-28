package com.niu.core.job.schedule;

import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
@JobProvider(key = "AutoClearPosterAndQrcode", name = "定时清理海报及二维码数据", describe = "定时清理海报及二维码数据", source = "core")
public class AutoClearPosterAndQrcode extends AbstractJobProvider implements IJobProvider {
    // 定义目录路径
    private static final String POSTER_DIR = "upload/poster";
    private static final String QRCODE_DIR = "upload/qrcode";

    // 日期格式化
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public void exec(JobExecutionContext context) {
        log.info("AutoClearPosterAndQrcode 定时清除二维码及海报数据开始 {}",
                DATE_FORMATTER.format(LocalDateTime.now()));

        try {
            // 清理海报目录
            log.info("AutoClearPosterAndQrcode尝试清理海报目录: {}", Paths.get(WebAppEnvs.get().webRootDownResource, POSTER_DIR));
            boolean posterResult = clearDirectory(Paths.get(WebAppEnvs.get().webRootDownResource, POSTER_DIR), true);
            log.info("AutoClearPosterAndQrcode海报目录清理结果: {}", posterResult ? "成功" : "失败");

            // 清理二维码目录
            log.info("AutoClearPosterAndQrcode尝试清理二维码目录: {}", QRCODE_DIR);
            boolean qrcodeResult = clearDirectory(Paths.get(WebAppEnvs.get().webRootDownResource, QRCODE_DIR), true);
            log.info("AutoClearPosterAndQrcode二维码目录清理结果: {}", qrcodeResult ? "成功" : "失败");

        } catch (Exception e) {
            log.error("AutoClearPosterAndQrcode 定时清除异常: {} 位置: {}:{}",
                    e.getMessage(),
                    e.getStackTrace()[0].getFileName(),
                    e.getStackTrace()[0].getLineNumber(),
                    e);
        }
    }

    /**
     * 清空指定目录下的所有文件和子目录
     * @param directory 目录路径
     * @param preserveDirectory 是否保留根目录
     * @return 是否成功执行
     */
    public boolean clearDirectory(Path directory, boolean preserveDirectory) {
        log.info("AutoClearPosterAndQrcode开始清理目录: {}", directory);

        // 检查目录是否存在
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            log.warn("AutoClearPosterAndQrcode目录不存在或不是有效目录: {}", directory);
            return false;
        }

        try {
            // 使用Java NIO的Files.walkFileTree递归删除
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        Files.delete(file);
                        log.debug("AutoClearPosterAndQrcode已删除文件: {}", file);
                    } catch (IOException e) {
                        log.error("AutoClearPosterAndQrcode删除文件失败: {}", file, e);
                        throw e;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null) {
                        throw exc;
                    }

                    // 如果不是根目录或者不保留根目录，则删除目录
                    if (!dir.equals(directory) || !preserveDirectory) {
                        try {
                            Files.delete(dir);
                            log.debug("AutoClearPosterAndQrcode已删除目录: {}", dir);
                        } catch (IOException e) {
                            log.error("AutoClearPosterAndQrcode删除目录失败: {}", dir, e);
                            throw e;
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            return true;
        } catch (IOException e) {
            log.error("AutoClearPosterAndQrcode清理目录过程中发生错误: {}", directory, e);
            return false;
        }
    }
}
