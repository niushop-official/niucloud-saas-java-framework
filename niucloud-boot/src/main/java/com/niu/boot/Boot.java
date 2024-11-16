package com.niu.boot;

import com.niu.boot.config.BootConfig;
import com.niu.boot.process.EnvironmentValidator;
import com.niu.boot.process.ProcessMonitor;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Boot {

    public static void main(String[] args) {
        BootConfig bootConfig = new BootConfig(args, "boot.yml");
        EnvironmentValidator environmentValidator = EnvironmentValidator.getInstance(bootConfig);
        if (environmentValidator.checkEnvironment()) {
            if (!environmentValidator.checkJreUsable()) {
                System.out.println("当前Java环境是:" + bootConfig.getJavaVersion() + ",请安装Java8的运行环境.");
                System.exit(0);
            }
            if (!environmentValidator.readAppEnv()) {
                System.out.println("请在 [" + bootConfig.getAppEnvPath() + "] 文件中进行配置.");
                System.exit(0);
            }
            if (!environmentValidator.checkMysqlUsable()) {
                System.out.println("请在 [" + bootConfig.getAppEnvPath() + "] 文件中进行配置, MySQL连接.");
                System.exit(0);
            }
            if (!environmentValidator.checkRedisUsable()) {
                System.out.println("请在 [" + bootConfig.getAppEnvPath() + "] 文件中进行配置, Redis连接.");
                System.exit(0);
            }
        }

        for (String arg: args) {
            if (arg.equals("--install-addon")) {
                try (RandomAccessFile raf = new RandomAccessFile("install_addon.dat", "rw");
                    FileChannel channel = raf.getChannel()) {
                    MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 100);
                    buffer.put("install-addon".getBytes());
                } catch (Exception e) {
                }
            }
        }

        bootConfig.printArguments();
        new ProcessMonitor(bootConfig).start();
        Runtime.getRuntime().addShutdownHook(new ProcessMonitor.Shutdown());
    }

}
