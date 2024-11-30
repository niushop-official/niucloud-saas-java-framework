package com.niu.core.common.config.executable.initialize;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.utils.file.PipeNameUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.service.core.addon.ICoreAddonInstallService;
import com.niu.core.service.core.app.ICoreAppService;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.File;

@Slf4j
public final class PipeCommandHelper {

    /**
     * 根据管道命令初始化执行
     */
    public static void initAppBasicByPipeCommand() {
        PipeNameUtils.AddonResult addonResult = PipeNameUtils.discoverInitCommand(GlobalConfig.applicationName);
        if (addonResult.getResult() == 1) {
            log.info("当前应用资源初始化===================>begin===================>");
            DataSource dataSource = (DataSource) SpringContext.getBean("dataSource");
            log.info("当前数据库连接池：dataSource=" + dataSource);
            ICoreAppService coreAppService = (ICoreAppService) SpringContext.getBean(ICoreAppService.class);
            try {
                coreAppService.initAppBasic(dataSource.getConnection());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("初始化应用程序基础配置错误：", e.getMessage());
            }
            log.info("当前应用资源初始化<===================ended<===================");
        }
    }

    /**
     * 处理管道命令
     */
    public static void doPipeCommand() {
        if (!GlobalConfig.runActive.equalsIgnoreCase("dev")) {
            try {
                PipeNameUtils.AddonResult addonResult = PipeNameUtils.checkBootCommand(GlobalConfig.applicationName);
                log.warn("----------------------------------------------------------------------");
                log.warn("addonResult: " + addonResult);
                log.warn("----------------------------------------------------------------------");
                if (PipeNameUtils.AddonResult.OPERATE_UNINSTALL.equals(addonResult.getOperate())) {
                    // 卸载操作，不需要回调
                    try {
                        String addonJarPath = WebAppEnvs.get().webRootDownJar + "/addon/" + addonResult.getAddon() + ".jar";
                        File addonJarFile = new File(addonJarPath);
                        if (addonJarFile.isFile() && addonJarFile.exists()) {
                            boolean isDeleted = addonJarFile.delete();
                            if (isDeleted) {
                                log.warn("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
                                log.warn("插件文件：" + addonJarFile + ",已被删除...........");
                                log.warn("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
                            } else {
                                log.warn("×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××");
                                log.warn("插件文件：" + addonJarFile + ",无法删除...........");
                                log.warn("×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××");
                            }
                        } else {
                            log.warn("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
                            log.info("插件文件：" + addonJarFile + ",已被boot卸载...........");
                            log.warn("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("卸载插件错误：", e);
                    }
                } else if (PipeNameUtils.AddonResult.OPERATE_ROLLBACK.equals(addonResult.getOperate())) {
                    // 回滚操作，需要回调
                    if (addonResult.getResult() == 1) {
                        ICoreAddonInstallService iCoreAddonInstallService = (ICoreAddonInstallService) SpringContext.getBean(ICoreAddonInstallService.class);
                        iCoreAddonInstallService.installResult(true, addonResult.getAddon(), addonResult.getMessage());
                    } else if (addonResult.getResult() == -1) {
                        ICoreAddonInstallService iCoreAddonInstallService = (ICoreAddonInstallService) SpringContext.getBean(ICoreAddonInstallService.class);
                        iCoreAddonInstallService.installResult(false, addonResult.getAddon(), addonResult.getMessage());
                    }
                } else if (addonResult.getOperate().endsWith("installed,")) {
                    // 升级成功，需要回调
                    if (addonResult.getResult() == 1) {
                        ICoreAddonInstallService iCoreAddonInstallService = (ICoreAddonInstallService) SpringContext.getBean(ICoreAddonInstallService.class);
                        iCoreAddonInstallService.installResult(true, addonResult.getAddon(), addonResult.getMessage());
                    } else if (addonResult.getResult() == -1) {
                        ICoreAddonInstallService iCoreAddonInstallService = (ICoreAddonInstallService) SpringContext.getBean(ICoreAddonInstallService.class);
                        iCoreAddonInstallService.installResult(false, addonResult.getAddon(), addonResult.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("error->", e.getMessage());
            }
        }
    }

}
