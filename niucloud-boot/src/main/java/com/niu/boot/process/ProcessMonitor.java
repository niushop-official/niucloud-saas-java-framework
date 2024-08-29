package com.niu.boot.process;

import com.niu.boot.config.BootConfig;
import com.niu.boot.utils.BootLogUtils;
import com.niu.boot.utils.ThreadUtils;

public class ProcessMonitor extends Thread {

    private static volatile boolean loop = true;

    private static ProcessManager bootProcessManager;

    private BootConfig bootConfig;

    /**
     * @param bootConfig
     */
    public ProcessMonitor(BootConfig bootConfig) {
        this.bootConfig = bootConfig;
        this.bootProcessManager = new ProcessManager(bootConfig);
    }

    @Override
    public void run() {
        while (loop) {
            try {
                bootProcessManager.watch();
//                BootLogUtils.log("应用状态为：" + bootProcessManager.getStatusMessage() + ".");
                if (bootProcessManager.getAtomicStatus().get() == 0) {
                    bootProcessManager.start();
                }
                ThreadUtils.sleepSeconds(bootConfig.timeToIdleSeconds());
            } catch (Throwable target) {
                System.err.println(target.getMessage());
            }
        }
    }

    public static class Shutdown extends Thread {

        @Override
        public void run() {
            loop = false;
            if (bootProcessManager != null) {
                bootProcessManager.stopForcibly();
            }
            BootLogUtils.log("main process exited.");
            System.exit(0);
        }
    }

}
