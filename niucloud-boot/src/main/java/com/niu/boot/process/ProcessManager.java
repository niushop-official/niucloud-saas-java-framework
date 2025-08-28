package com.niu.boot.process;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.boot.config.BootConfig;
import com.niu.boot.dto.UpgradeDTO;
import com.niu.boot.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    public void watch() throws Exception {
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
        } else if (pipeContent.startsWith("install,")) {
            // ============================================================================//
            // 正常安装情况，进程也在运行中
            // ============================================================================//
            if (getAtomicStatus().get() == 3) {
                String upgradingCommand = pipeContent.replaceAll("install,", "installing,");
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
            // 正常安装情况，进程已经处于停止状态，这时候也需要启动起来继续安装任务
            // ============================================================================//
            if (getAtomicStatus().get() == 5 || getAtomicStatus().get() == 6) {
                String upgradingCommand = pipeContent.replaceAll("install,", "installing,");
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
        } else if (pipeContent.startsWith("installing,")) {
            // ============================================================================//
            // 执行安装情况，可以正常启动，进入安装完成，同时做出通知
            // ============================================================================//
            if (getAtomicStatus().get() == 3) {
                long currentTime = System.currentTimeMillis();
                BootLogUtils.log("插件安装成功, 耗时：" + ((currentTime - upgradeTime) / 60) + " s.");
                String upgradedCommand = pipeContent.replaceAll("installing,", "installed,");
                this.writePipe(upgradedCommand);
                this.upgradeCommand = "";
                this.upgradeTime = 0;
            }
            // ============================================================================//
            // 执行安装情况，不能正常启动，进入重启或者回滚后重启，同时做出通知
            // ============================================================================//
            if (getAtomicStatus().get() == 5) {
                if (this.upgradeCount < this.bootConfig.failedRestartCount()) {
                    this.upgradeCount = this.upgradeCount + 1;
                    getAtomicStatus().set(0);
                    this.restartWait();
                    long currentTime = System.currentTimeMillis();
                    BootLogUtils.log("第" + this.upgradeCount + "次启动, 耗时：" + ((currentTime - upgradeTime) / 60) + " s.");
                } else {
                    String rollbackCommand = this.upgradeCommand.replaceAll("installing,", "rollback,");
                    this.writePipe(rollbackCommand);
                    AddonRollback.execute(rollbackCommand, this.bootConfig.getJarDir());
                    long currentTime = System.currentTimeMillis();
                    BootLogUtils.log("插件安装失败, 耗时：" + ((currentTime - upgradeTime) / 60) + " s, 进入回滚操作.");
                    this.upgradeCommand = "";
                    this.restartWait();
                }
            }
        } else if (pipeContent.startsWith("installed,")) {
            BootLogUtils.log("安装成功后, 已正常启动服务.");
        } else if (pipeContent.startsWith("rollback,")) {
            if (getAtomicStatus().get() == 3) {
                BootLogUtils.log("安装回滚后, 已正常启动服务.");
            }
            if (getAtomicStatus().get() == 5) {
                BootLogUtils.log(".......................................................");
                BootLogUtils.log("安装回滚后, 启动失败，急需要人工干预处理.");
            }
        } else if (pipeContent.startsWith("upgrade,")) {
            this.resetPipe();
            this.stop();

            String[] data = pipeContent.split(",");
            String upgradeKey = data[1];
            String backupKey = data[2];

            String command = "upgradesuccess,";
            try {
                this.coverUpgradeFile(upgradeKey, backupKey);
            } catch (Exception e) {
                restoreBackup(backupKey);
                command = "upgradefail,升级覆盖文件失败错误原因：" + e.getMessage();
                BootLogUtils.log("升级覆盖文件失败,错误原因：" + e.getMessage());
                e.printStackTrace();
            }
            if (!command.startsWith("upgradesuccess,")) {
                try {
                    restoreBackup(backupKey);
                } catch (Exception e) {}
            }
            this.writePipe(command);
            this.start();
        } else if (pipeContent.startsWith("restorebackup,")) {
            this.resetPipe();
            this.stop();

            String[] data = pipeContent.split(",");
            String backupKey = data[1];
            String command = "restorebackupsuccess,";

            try {
                restoreBackup(backupKey);
            } catch (Exception e) {
                command = "restorebackupfail,升级变更文件回滚失败错误原因：" + e.getMessage();
                BootLogUtils.log("升级变更文件回滚失败错误原因：" + e.getMessage());
                e.printStackTrace();
            }

            this.writePipe(command);
            this.start();
        }
    }

    private void coverUpgradeFile(String upgradeKey, String backupKey) throws Exception {
        File upgradeDir = new File(bootConfig.getRootPath(), "webroot/runtime/upgrade/" + upgradeKey);
        System.out.println("upgrade file dir：" + upgradeDir.getPath());
        if (!upgradeDir.exists()) throw new Exception("升级文件不存在");

        File jsonFile = new File(upgradeDir, "upgrade.json");
        if (!jsonFile.exists()) throw new Exception("升级文件不存在");

        UpgradeDTO upgrade = JSONUtil.toBean(JSONUtil.parseObj(JsonLoadUtils.loadJsonString(jsonFile)), UpgradeDTO.class);
        upgrade.getContent().forEach(item -> {
            String appKey = item.getApp().getAppKey();
            File codeDir = new File(upgradeDir, "/download/" + appKey);

            // 判断目录存在并且不为空
            if (codeDir.exists() && codeDir.listFiles().length > 0) {
                item.getVersionList().stream().collect(Collectors.collectingAndThen(
                    Collectors.toList(),
                    l -> {
                        Collections.reverse(l);
                        return l.stream();
                    })).forEach(version -> {
                    // 如果是框架
                    File rootDir = null;
                    if (appKey.equals("niucloud-admin")) {
                        rootDir = new File(bootConfig.getRootPath());
                    } else {
                        rootDir = new File(bootConfig.getRootPath(), "webroot/addon/" + appKey);
                    }

                    // 处理文件变更记录
                    File changeRecord = new File(codeDir,  version.getVersionNo() + ".txt");
                    if (changeRecord.exists()) {
                        // 移除新版本删除的文件
                        List<String> change = null;
                        try {
                            change = Arrays.asList(FileUtils.readFileToString(changeRecord, "UTF-8").split("\n"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (ObjectUtil.isNotEmpty(change)) {
                            for (String line : change) {
                                String[] content = line.split(" ");
                                if (content[0].equals("-")) {
                                    (new File(rootDir, content[2])).deleteOnExit();
                                }
                            }
                        }
                    }
                    // 覆盖更新文件
                    if (appKey.equals("niucloud-admin")) {
                        IOFileFilter envFile = FileTools.createExclusionFilter(new String[]{
                            "application-prod.yml",
                            ".env.development",
                            ".env.production",
                            ".env.dev",
                            ".env.product",
                            "web-app-boot-exec.jar"
                        });
                        File[] childFile = new File(codeDir, version.getVersionNo()).listFiles();
                        for (File file : childFile) {
                            if (file.isDirectory()) {
                                if (new File(bootConfig.getRootPath(), file.getName()).exists()) {
                                    try {
                                        FileUtils.copyDirectory(file, new File(bootConfig.getRootPath(), file.getName()), envFile);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            } else {
                                try {
                                    FileUtils.copyFile(file, new File(bootConfig.getRootPath(), file.getName()));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    } else {
                        File changeDir = new File(codeDir, version.getVersionNo());
                        if (changeDir.exists()) {
                            try {
                                FileUtils.copyDirectory(changeDir, rootDir);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });

                if (!appKey.equals("niucloud-admin")) {
                    File projectDir = new File(bootConfig.getRootPath(), "niucloud-addon/" + appKey);;
                    if (projectDir.exists()) {
                        try {
                            FileUtils.copyDirectory(new File(bootConfig.getRootPath(), "webroot/addon/" + appKey + "/java"), projectDir);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // 合并resource
                    try {
                        FileUtils.copyDirectory(new File(bootConfig.getRootPath(), "webroot/addon/" + appKey + "/resource"), new File(bootConfig.getRootPath(), "webroot/resource/" + appKey));
                    } catch (IOException e) {
                    }
                    // 更新插件jar包
                    File jarFile = new File(bootConfig.getRootPath(), "webroot/addon/" + appKey + "/jar/" + appKey + ".jar");
                    if (jarFile.exists()) {
                        try {
                            FileUtils.copyFile(jarFile, new File(bootConfig.getRootPath(), "webroot/jar/addon/" + appKey + ".jar"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }

    private void restoreBackup(String backupKey) throws Exception {
        File backupDir = new File(bootConfig.getRootPath(), "webroot/runtime/upgrade/" + backupKey + "/backup/code");
        if (!backupDir.exists()) throw new Exception("备份文件不存在");

        FileUtils.copyDirectory(backupDir, new File(bootConfig.getRootPath()));
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
                    processBuilder = new ProcessBuilder(javaCmd, "-Dfile.encoding=UTF-8", "-jar", jarPath, jvmArg1, jvmArg2);
                } else {
                    String jvmArgs = "--spring.profiles.active=dev";
                    processBuilder = new ProcessBuilder(javaCmd, "-Dfile.encoding=UTF-8", "-jar", jarPath, jvmArgs);
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
