package com.niu.boot;

import com.niu.boot.config.BootConfig;
import com.niu.boot.process.EnvironmentValidator;
import com.niu.boot.process.ProcessMonitor;

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
        bootConfig.printArguments();
        new ProcessMonitor(bootConfig).start();
        Runtime.getRuntime().addShutdownHook(new ProcessMonitor.Shutdown());
    }

}
