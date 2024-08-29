package com.niu.boot.process;

import com.niu.boot.config.BootConfig;
import com.niu.boot.utils.BIOUtils;
import com.niu.boot.utils.BootLogUtils;
import com.niu.boot.utils.PipeNameUtils;
import com.niu.boot.utils.ThreadUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessManager {

    protected BootConfig bootConfig;

    protected Process process;

    // 0====>初始化状态
    // 1====>进程运行开始
    // 2====>进程运行成功
    // 3====>服务正常运行
    // 4====>进程运行完成
    // 5====>运行结束进程
    // 6====>停止操作结束进程
    protected AtomicInteger atomicStatus = new AtomicInteger(0);
    // 状态消息
    protected String statusMessage;

    // 重启次数
    protected int restartCount;

    protected long upgradeTime;

    protected int upgradeCount = 1;

    protected String upgradeCommand;

    /**
     * @param bootConfig
     */
    public ProcessManager(BootConfig bootConfig) {
        this.bootConfig = bootConfig;
        getAtomicStatus().set(0);
    }

    /**
     * 监视
     */
    public void watch() {
        String pipeContent = this.readPipe();
        if (!"".equals(pipeContent)) {
            BootLogUtils.log("命名管道接收信息为：" + pipeContent);
        }
        if ("".equals(pipeContent)) {
            // ignore
        } else if ("start".equals(pipeContent)) {
            if (getAtomicStatus().get() == 0 || getAtomicStatus().get() == 5 || getAtomicStatus().get() == 6) {
                this.resetPipe();
                this.start();
            }
        } else if ("start,init".equals(pipeContent)) {
            if (getAtomicStatus().get() == 0) {
                String startInitCommand = "start,initializing";
                this.writePipe(startInitCommand);
                this.start();
            }
        } else if ("stop".equals(pipeContent)) {
            this.resetPipe();
            this.stop();
            getAtomicStatus().set(6);
        } else if ("restart".equals(pipeContent)) {
            this.resetPipe();
            this.stopForcibly();
            if (this.process != null) {
                this.process = null;
            }
            ThreadUtils.sleepSeconds(2);
            this.start();
            ThreadUtils.sleepSeconds(5);
        } else if (pipeContent.startsWith("uninstall,")) {
            this.stopForcibly();
            if (this.process != null) {
                this.process = null;
            }
            ThreadUtils.sleepSeconds(2);
            String uninstalledCommand = pipeContent.replaceAll("uninstall,", "uninstalled,");
            this.writePipe(uninstalledCommand);
            AddonRollback.execute(uninstalledCommand, this.bootConfig.getJarDir());
            this.start();
            ThreadUtils.sleepSeconds(5);
        } else if (pipeContent.startsWith("upgrade,")) {
            // ============================================================================//
            // 正常升级情况，进程也在运行中
            // ============================================================================//
            if (getAtomicStatus().get() == 3) {
                String upgradingCommand = pipeContent.replaceAll("upgrade,", "upgrading,");
                this.writePipe(upgradingCommand);
                this.upgradeCount = 1;
                this.upgradeTime = System.currentTimeMillis();
                this.upgradeCommand = upgradingCommand;
                this.stopForcibly();
                if (this.process != null) {
                    this.process = null;
                    ThreadUtils.sleepSeconds(1);
                }
                this.start();
                ThreadUtils.sleepSeconds(10);
            }
            // ============================================================================//
            // 正常升级情况，进程已经处于停止状态，这时候也需要启动起来继续升级任务
            // ============================================================================//
            if (getAtomicStatus().get() == 5 || getAtomicStatus().get() == 6) {
                String upgradingCommand = pipeContent.replaceAll("upgrade,", "upgrading,");
                this.writePipe(upgradingCommand);
                this.upgradeCount = 1;
                this.upgradeTime = System.currentTimeMillis();
                this.upgradeCommand = upgradingCommand;
                this.stopForcibly();
                if (this.process != null) {
                    this.process = null;
                    ThreadUtils.sleepSeconds(1);
                }
                this.start();
                ThreadUtils.sleepSeconds(10);
            }
        } else if (pipeContent.startsWith("upgrading,")) {
            // ============================================================================//
            // 执行升级情况，可以正常启动，进入升级完成，同时做出通知
            // ============================================================================//
            if (getAtomicStatus().get() == 3) {
                long currentTime = System.currentTimeMillis();
                BootLogUtils.log("插件升级成功, 耗时：" + ((currentTime - upgradeTime) / 60) + " s.");
                String upgradedCommand = pipeContent.replaceAll("upgrading,", "upgraded,");
                this.writePipe(upgradedCommand);
                this.upgradeCommand = "";
                this.upgradeTime = 0;
            }
            // ============================================================================//
            // 执行升级情况，不能正常启动，进入重启或者回滚后重启，同时做出通知
            // ============================================================================//
            if (getAtomicStatus().get() == 5) {
                if (this.upgradeCount < this.bootConfig.failedRestartCount()) {
                    this.upgradeCount = this.upgradeCount + 1;
                    getAtomicStatus().set(0);
                    this.restartWait();
                    long currentTime = System.currentTimeMillis();
                    BootLogUtils.log("第" + this.upgradeCount + "次启动, 耗时：" + ((currentTime - upgradeTime) / 60) + " s.");
                } else {
                    String rollbackCommand = this.upgradeCommand.replaceAll("upgrading,", "rollback,");
                    this.writePipe(rollbackCommand);
                    AddonRollback.execute(rollbackCommand, this.bootConfig.getJarDir());
                    long currentTime = System.currentTimeMillis();
                    BootLogUtils.log("插件升级失败, 耗时：" + ((currentTime - upgradeTime) / 60) + " s, 进入回滚操作.");
                    this.upgradeCommand = "";
                    this.restartWait();
                }
            }
        } else if (pipeContent.startsWith("upgraded,")) {
            BootLogUtils.log("升级成功后, 已正常启动服务.");
        } else if (pipeContent.startsWith("rollback,")) {
            if (getAtomicStatus().get() == 3) {
                BootLogUtils.log("升级回滚后, 已正常启动服务.");
            }
            if (getAtomicStatus().get() == 5) {
                BootLogUtils.log(".......................................................");
                BootLogUtils.log("升级回滚后, 启动失败，急需要人工干预处理.");
            }
        }
    }

    /**
     * 重置管道
     */
    private void resetPipe() {
        this.writePipe("");
    }

    /**
     * @param command
     */
    private void writePipe(String command) {
        String pipePath = bootConfig.getTmpHome() + "/pipe/";
        String pipeName = bootConfig.applicationName();
        String encoding = bootConfig.getEncoding();
        PipeNameUtils.writeContext(pipePath, pipeName, encoding, command);
    }

    /**
     * @return
     */
    private String readPipe() {
        String pipePath = bootConfig.getTmpHome() + "/pipe/";
        String pipeName = bootConfig.applicationName();
        String encoding = bootConfig.getEncoding();
        return PipeNameUtils.readContext(pipePath, pipeName, encoding);
    }

    /**
     * 启动
     */
    public void start() {
        getAtomicStatus().set(1);
        new Thread(startProcessThread()).start();
    }

    /**
     * @return
     */
    protected Runnable startProcessThread() {
        return () -> {
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                String applicationName = bootConfig.applicationName();
                String javaCmd = bootConfig.getJavaExec();
                String jarPath = this.getJarExecPath();
                ProcessBuilder processBuilder = null;
                if ("prod".equals(bootConfig.environment())) {
                    String jvmArg1 = "--spring.profiles.active=prod";
                    String jvmArg2 = "--spring.config.location=file:" + bootConfig.getApplicationYml() + "," + bootConfig.getApplicationYmlBoot();
                    processBuilder = new ProcessBuilder(javaCmd, "-jar", jarPath, jvmArg1, jvmArg2);
                } else {
                    String jvmArgs = "--spring.profiles.active=dev";
                    processBuilder = new ProcessBuilder(javaCmd, "-jar", jarPath, jvmArgs);
                }
                processBuilder.redirectErrorStream(true);
                processBuilder.environment().put("JAVA_OPTS", bootConfig.getJvmOpts(applicationName));
                this.process = processBuilder.start();
                String encode = bootConfig.getPrintEncode();
                inputStreamReader = new InputStreamReader(process.getInputStream(), encode);
                bufferedReader = new BufferedReader(inputStreamReader);
                getAtomicStatus().set(2);
                BootLogUtils.resetLog(this.bootConfig);
                String line;
                boolean startupLog = false;
                while ((line = bufferedReader.readLine()) != null) {
                    if (this.bootConfig.printLog()) {
                        System.out.println(line);
                    }
                    if (startupLog) {
                        // 记录启动日志
                        BootLogUtils.recordLog(this.bootConfig, line);
                    }
                    if (line.contains(this.bootConfig.serviceStartingHookFlag())) {
                        startupLog = true;
                    }
                    if (line.contains(this.bootConfig.serviceStartedSuccessfullyFlag())) {
                        getAtomicStatus().set(3);
                        startupLog = false;
                    }
                }
                if (process != null) {
                    process.waitFor();
                    getAtomicStatus().set(4);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BIOUtils.close(bufferedReader);
                BIOUtils.close(inputStreamReader);
                BIOUtils.destroy(process);
                getAtomicStatus().set(5);
            }
        };
    }

    /**
     * jar文件路径
     *
     * @return
     */
    public String getJarExecPath() {
        if ("dev".equals(this.bootConfig.environment())) {
            return this.bootConfig.jarPath() + "/" + this.bootConfig.jarName();
        } else {
            return this.bootConfig.getWorkDir() + "/" + this.bootConfig.jarName();
        }
    }

    /**
     * jar目录路径s
     *
     * @return
     */
    public String getJarDir() {
        if ("dev".equals(this.bootConfig.environment())) {
            return this.bootConfig.jarPath() + "/";
        } else {
            return this.bootConfig.getWorkDir() + "/";
        }
    }

    /**
     * 重启
     */
    public void restart() {
        restartCount++;
        this.stopForcibly();
        this.start();
    }

    /**
     * 重启并等待
     */
    public void restartWait() {
        this.stopForcibly();
        if (this.process != null) {
            this.process = null;
            ThreadUtils.sleepSeconds(1);
        }
        this.start();
        ThreadUtils.sleepSeconds(10);
    }

    /**
     * 获取状态
     *
     * @return
     */
    public AtomicInteger getAtomicStatus() {
        return atomicStatus;
    }

    /**
     * 停止
     */
    public void stop() {
        BIOUtils.destroy(process);
        this.getAtomicStatus().set(6);
    }

    /**
     * 强制停止
     */
    public void stopForcibly() {
        BIOUtils.destroyForcibly(process);
        this.getAtomicStatus().set(6);
    }

    /**
     * @return
     */
    public String getStatusMessage() {
        if (this.getAtomicStatus().get() == 0) {
            return "初始化";
        } else if (this.getAtomicStatus().get() == 1) {
            return "进程运行开始";
        } else if (this.getAtomicStatus().get() == 2) {
            return "进程运行成功";
        } else if (this.getAtomicStatus().get() == 3) {
            return "服务正常运行";
        } else if (this.getAtomicStatus().get() == 4) {
            return "进程运行完成";
        } else if (this.getAtomicStatus().get() == 5) {
            return "进程运行结束";
        } else if (this.getAtomicStatus().get() == 6) {
            return "正常结束进程";
        }
        return "其它";
    }

}
