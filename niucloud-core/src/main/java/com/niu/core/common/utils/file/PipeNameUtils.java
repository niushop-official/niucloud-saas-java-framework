package com.niu.core.common.utils.file;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.system.RuntimeTools;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.service.admin.upgrade.IUpgradeService;
import com.niu.core.service.admin.upgrade.vo.UpgradeTaskVo;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class PipeNameUtils {

    /**
     * @param applicationName
     * @param addonName
     */
    public static void noticeBootRestart(String applicationName, String addonName) {
        noticeBoot(applicationName, "install", addonName);
    }

    /**
     * @param applicationName
     * @param addonName
     */
    public static void noticeBootRestartByUninstall(String applicationName, String addonName) {
        noticeBoot(applicationName, "uninstall", addonName);
    }

    /**
     *
     * @param applicationName
     * @param addonName
     */
    public static void noticeBootRestartByUpgrade(String applicationName, String addonName, String dir, String version) {
        noticeBootCommand(applicationName, "upgrade," + addonName + "," + dir + "," + version);
    }


    public static void noticeBootRestartByUpgradeRollback(String applicationName, String addonName, String dir) {
        noticeBootCommand(applicationName, "upgraderollback," + addonName + "," + dir);
    }

    /**
     * @param applicationName
     * @param command
     * @param addonName
     */
    public static void noticeBoot(String applicationName, String command, String addonName) {
        String tmpHome = RuntimeTools.getTmpHome();
        String pipePath = tmpHome + "/pipe/" + applicationName;
        String pipeContent = command + "," + addonName;
        PipeNameUtils.writeContext(pipePath, pipeContent);
    }

    /**
     * @param applicationName
     * @param command
     */
    public static void noticeBootCommand(String applicationName, String command) {
        String tmpHome = RuntimeTools.getTmpHome();
        String pipePath = tmpHome + "/pipe/" + applicationName;
        PipeNameUtils.writeContext(pipePath, command);
    }

    /**
     * @param applicationName
     * @return
     */
    public static AddonResult discoverInitCommand(String applicationName) {
        String tmpHome = RuntimeTools.getTmpHome();
        String pipePath = tmpHome + "/pipe/" + applicationName;
        log.info("管道：" + new File(pipePath).getAbsolutePath());
        AddonResult addonResult = new AddonResult();
        addonResult.setResult(-1);
        String command = PipeNameUtils.getBootCommand(GlobalConfig.applicationName);
        if (command.startsWith("start,initializing")) {
            addonResult.setResult(1);
            addonResult.setOperate(AddonResult.OPERATE_INIT);
            PipeNameUtils.writeContext(pipePath, "");
        }
        return addonResult;
    }

    /**
     * @param applicationName
     * @return
     */
    public static AddonResult checkBootCommand(String applicationName) {
        AddonResult addonResult = new AddonResult();
        String command = PipeNameUtils.getBootCommand(GlobalConfig.applicationName);
        log.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.warn("命名管道内容为：" + command);
        log.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        if (command.startsWith("uninstall,")) {
            // 不做处理，不需要重置命令，这时boot还未执行
            String addon = command.replaceAll("uninstall,", "");
            addonResult.setResult(-1);
            addonResult.setOperate(AddonResult.OPERATE_UNINSTALL);
            addonResult.setAddon(addon);
        } else if (command.startsWith("uninstalled,")) {
            PipeNameUtils.noticeBootCommand(applicationName, "");
            String addon = command.replaceAll("uninstalled,", "");
            addonResult.setResult(1);
            addonResult.setOperate(AddonResult.OPERATE_UNINSTALL);
            addonResult.setAddon(addon);
            // 卸载完成
        } else if (command.startsWith("installing,")) {
            PipeNameUtils.noticeBootCommand(applicationName, "");
            String addon = command.replaceAll("installing,", "");
            addonResult.setResult(1);
            addonResult.setOperate(AddonResult.OPERATE_ROLLBACK);
            addonResult.setAddon(addon);
        } else if (command.startsWith("installed,")) {
            PipeNameUtils.noticeBootCommand(applicationName, "");
            String addon = command.replaceAll("installed,", "");
            addonResult.setResult(1);
            addonResult.setOperate(AddonResult.OPERATE_ROLLBACK);
            addonResult.setAddon(addon);
        } else if (command.startsWith("rollback,")) {
            PipeNameUtils.noticeBootCommand(applicationName, "");
            String addon = command.replaceAll("rollback,", "");
            addonResult.setResult(-1);
            addonResult.setOperate(AddonResult.OPERATE_ROLLBACK);
            addonResult.setAddon(addon);
        } else if (command.startsWith("upgrade,") || command.startsWith("upgraderollback,")) {
            PipeNameUtils.noticeBootCommand(applicationName, "");
        } else if (command.startsWith("upgradefail,")) {
            PipeNameUtils.noticeBootCommand(applicationName, "");
            IUpgradeService upgradeService = SpringContext.bean(IUpgradeService.class);
            UpgradeTaskVo task = upgradeService.getUpgradeTask();
            if (ObjectUtil.isNotEmpty(task) && ObjectUtil.isNotEmpty(task.getStatus()) && task.getStatus().equals("restarting")) {
                String msg = command.replaceAll("upgradefail,", "");
                task.setStatus("restarted");
                task.getError().add(msg);
                upgradeService.setUpgradeTaskCache(task);
                upgradeService.upgradeErrorHandle(task);
            }
        } else if (command.startsWith("upgradesuccess,")) {
            PipeNameUtils.noticeBootCommand(applicationName, "");
            IUpgradeService upgradeService = SpringContext.bean(IUpgradeService.class);
            UpgradeTaskVo task = upgradeService.getUpgradeTask();
            if (ObjectUtil.isNotEmpty(task) && ObjectUtil.isNotEmpty(task.getStatus()) && task.getStatus().equals("restarting")) {
                task.setStatus("restarted");
                upgradeService.setUpgradeTaskCache(task);
            }
        } else if (command.startsWith("upgraderollbackfail,")) {
            PipeNameUtils.noticeBootCommand(applicationName, "");
            IUpgradeService upgradeService = SpringContext.bean(IUpgradeService.class);
            UpgradeTaskVo task = upgradeService.getUpgradeTask();
            if (ObjectUtil.isNotEmpty(task) && ObjectUtil.isNotEmpty(task.getStatus()) && task.getStatus().equals("restarting")) {
                String msg = command.replaceAll("upgraderollbackfail,", "");
                task.setStatus("restarted");
                task.getError().add(msg);
                upgradeService.setUpgradeTaskCache(task);
            }
        } else if (command.startsWith("upgraderollbacksuccess,")) {
            PipeNameUtils.noticeBootCommand(applicationName, "");
            IUpgradeService upgradeService = SpringContext.bean(IUpgradeService.class);
            UpgradeTaskVo task = upgradeService.getUpgradeTask();
            if (ObjectUtil.isNotEmpty(task) && ObjectUtil.isNotEmpty(task.getStatus()) && task.getStatus().equals("restarting")) {
                task.setStatus("restarted");
                upgradeService.setUpgradeTaskCache(task);
            }
        } else {
            addonResult.setResult(0);
        }
        return addonResult;
    }

    public static class AddonResult {

        // -1： 回滚操作；
        // 0： 正常启动
        // 1： 成功
        private int result = 0;

        public static final String OPERATE_INIT = "init";
        public static final String OPERATE_UNINSTALL = "uninstall";
        public static final String OPERATE_ROLLBACK = "rollback";

        // uninstall, rollback
        private String operate = "rollback";

        private String addon;

        private String message = "";

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public String getOperate() {
            return operate;
        }

        public void setOperate(String operate) {
            this.operate = operate;
        }

        public String getAddon() {
            return addon;
        }

        public void setAddon(String addon) {
            this.addon = addon;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "AddonResult{" +
                    "result=" + result +
                    ", operate='" + operate + '\'' +
                    ", addon='" + addon + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    /**
     * 路径
     *
     * @param applicationName
     * @return
     */
    public static String getBootCommand(String applicationName) {
        String tmpHome = RuntimeTools.getTmpHome();
        String pipePath = tmpHome + "/pipe/" + applicationName;
        return readContext(pipePath);
    }

    /**
     * @param pipeFile
     * @return
     */
    public static String readContext(String pipeFile) {
        File file = new File(pipeFile);
        return readContext(file.getParent(), file.getName());
    }

    /**
     * @param pipePath
     * @param pipeName
     * @return
     */
    public static String readContext(String pipePath, String pipeName) {
        return readContext(pipePath, pipeName, "UTF-8");
    }

    /**
     * @param pipePath
     * @param encoding
     * @return
     */
    public static String readContext(String pipePath, String pipeName, String encoding) {
        StringBuilder stringBuilder = new StringBuilder();
        Path pipe = Paths.get(pipePath);
        File pipeDir = pipe.toFile();
        if (!pipeDir.exists()) {
            try {
                pipeDir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File pipeFile = new File(pipeDir.getAbsolutePath() + "/" + pipeName);
        if (!pipeFile.exists()) {
            try {
                pipeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(pipeFile);
                 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, encoding);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (Exception ee) {

            }
        }
        return stringBuilder.toString();
    }

    /**
     * @param pipeFile
     * @param command
     */
    public static void writeContext(String pipeFile, String command) {
        File file = new File(pipeFile);
        writeContext(file.getParent(), file.getName(), command);
    }

    /**
     * @param pipePath
     * @param pipeName
     * @param command
     */
    public static void writeContext(String pipePath, String pipeName, String command) {
        writeContext(pipePath, pipeName, "UTF-8", command);
    }

    /**
     * @param pipePath
     * @param pipeName
     * @param fileEncoding
     * @param command
     */
    public static void writeContext(String pipePath, String pipeName, String fileEncoding, String command) {
        Path pipe = Paths.get(pipePath);
        File pipeDir = pipe.toFile();
        if (!pipeDir.exists()) {
            try {
                pipeDir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File pipeFile = new File(pipeDir.getAbsolutePath() + "/" + pipeName);
        if (!pipeFile.exists()) {
            try {
                pipeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(pipeFile);
             OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, fileEncoding)) {
            writer.write(command);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
