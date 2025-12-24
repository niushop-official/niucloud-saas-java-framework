package com.niu.core.service.core.addon.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.file.AddonUpgradeHelper;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.entity.addon.Addon;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.service.admin.addon.IAddonDevelopBuildService;
import com.niu.core.service.admin.niucloud.ICloudBuildService;
import com.niu.core.service.core.addon.*;
import com.niu.core.service.core.app.tools.SQLScriptRunnerTools;
import com.niu.core.service.core.schedule.ICoreScheduleService;
import com.niu.core.service.core.sys.ICoreMenuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CoreAddonInstallServiceImpl extends CoreAddonBaseService implements ICoreAddonInstallService {

    @Resource
    Cached cached;

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    ICoreMenuService coreMenuService;

    @Resource
    AddonMapper addonMapper;

    @Resource
    AddonInstallTools addonInstallTools;

    @Resource
    ICloudBuildService cloudBuildService;

    @Resource
    ICoreScheduleService coreScheduleService;

    @Resource
    IAddonDevelopBuildService addonDevelopBuildService;

    /**
     * 插件安装检测
     *
     * @param addon
     * @return
     */
    @Override
    public JSONObject installCheck(String addon) {
        Boolean installCheck = true;
        JSONObject checkResult = new JSONObject();
        String rootPath = "";
        String runtimePath = "";
        JSONArray readableDir = new JSONArray();
        JSONArray writeDir = new JSONArray();

        if (WebAppEnvs.get().envType.equals("dev")) {
            rootPath = WebAppEnvs.get().projectRoot + "/";
            runtimePath = rootPath;

            readableDir.put(new JSONObject().set("dir", rootPath + "niucloud-addon").set("status", true));
            writeDir.put(new JSONObject().set("dir", rootPath + "niucloud-addon").set("status", true));
            readableDir.put(new JSONObject().set("dir", rootPath + "webroot").set("status", true));
            writeDir.put(new JSONObject().set("dir", rootPath + "webroot").set("status", true));
        } else {
            rootPath = WebAppEnvs.get().webRoot + "/";
            runtimePath = rootPath + "runtime/";

            readableDir.put(new JSONObject().set("dir", runtimePath).set("status", true));
            writeDir.put(new JSONObject().set("dir", runtimePath).set("status", true));
        }

        readableDir.put(new JSONObject().set("dir", runtimePath + "admin").set("status", true));
        readableDir.put(new JSONObject().set("dir", runtimePath + "uni-app").set("status", true));
        readableDir.put(new JSONObject().set("dir", runtimePath + "web").set("status", true));

        writeDir.put(new JSONObject().set("dir", runtimePath + "admin").set("status", true));
        writeDir.put(new JSONObject().set("dir", runtimePath + "uni-app").set("status", true));
        writeDir.put(new JSONObject().set("dir", runtimePath + "web").set("status", true));

        for (int i = 0; i < readableDir.size(); i++) {
            JSONObject dir = readableDir.getJSONObject(i);
            dir.set("status", new File(dir.getStr("dir")).canRead());
            dir.set("dir", dir.getStr("dir").replace(rootPath, ""));
            readableDir.set(i, dir);
            if (!dir.getBool("status")) installCheck = false;
        }

        for (int i = 0; i < writeDir.size(); i++) {
            JSONObject dir = writeDir.getJSONObject(i);
            dir.set("status", new File(dir.getStr("dir")).canWrite());
            dir.set("dir", dir.getStr("dir").replace(rootPath, ""));
            writeDir.set(i, dir);
            if (!dir.getBool("status")) installCheck = false;
        }

        checkResult.put("is_pass", installCheck);
        checkResult.put("dir", new JSONObject().set("is_readable", readableDir).set("is_write", writeDir));
        checkResult.put("file_permission_is_pass", installCheck);

        cached.put("addon_install_check", installCheck);
        return checkResult;
    }

    /**
     * 安装插件
     *
     * @param addon
     * @return
     */
//    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject install(String addon, String mode) {
        Boolean installCheck = (Boolean) cached.get("addon_install_check");
        if (installCheck == null || !installCheck) {
            throw new CommonException("安装校验未通过");
        }
        Addon addonObject = coreAddonService.getInfoByKey(addon);
        if (ObjectUtil.isNotEmpty(addonObject)) {
            throw new CommonException("当前插件已安装,不能重复安装");
        }
        JSONObject installTask = this.getInstallTask();
        if (ObjectUtil.isNotEmpty(installTask)) {
            throw new CommonException("有正在安装中的插件");
        }
        installTask = new JSONObject();
        installTask.put("mode", mode);
        installTask.put("addon", addon);
        installTask.put("status", "installing");
        installTask.put("step", new JSONArray());
        setInstallTask(installTask);

        try {
            this.installDir();
            this.installDepend();
            this.installUniapp(addon);
            if (mode.equals("local")) {
                this.handleAddonInstall();
            } else {
                this.cloudInstall();
            }
            return this.getInstallTask();
        } catch (Exception e) {
            e.printStackTrace();
            this.installExceptionHandle();
            this.cancleInstall();
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 获取安装任务
     *
     * @return
     */
    public JSONObject getInstallTask() {
        if (cached.get("addon_install_task") == null) return null;
        JSONObject installTask = (JSONObject) cached.get("addon_install_task");
        return installTask;
    }

    public void setInstallTask(JSONObject installTask) {
        cached.put("addon_install_task", installTask);
    }

    /**
     * 取消安装任务
     */
    public void cancleInstall() {
        if (ObjectUtil.isNotEmpty(this.getInstallTask())) {
            cached.remove("addon_install_task");
        }
    }

    /**
     * 云安装
     *
     */
    private void cloudInstall() {
        // 备份前端文件
        try {
            File tempDir = new File(WebAppEnvs.get().webRootDownRuntime + "cloud_install/");
            if (tempDir.exists()) FileUtils.deleteDirectory(tempDir);
            FileTools.createDirs(tempDir.getPath());
            FileUtils.copyDirectory(new File(WebAppEnvs.get().webRootDownPublic), tempDir);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
        cloudBuildService.build("install");
    }

    /**
     * 安装插件代码
     */
    private void installDir() {
        JSONObject installTask = this.getInstallTask();
        addonInstallTools.installDir(installTask.getStr("addon"));
        installTask.getJSONArray("step").add("installDir");
        setInstallTask(installTask);
    }

    /**
     * 处理uniapp
     *
     * @param addon
     */
    private void installUniapp(String addon) {
        JSONObject installTask = this.getInstallTask();
        addonInstallTools.installUniapp(addon);
        installTask.getJSONArray("step").add("installUniapp");
        setInstallTask(installTask);
    }

    /**
     * 合并依赖
     */
    private void installDepend() {
        addonInstallTools.installDepend(this.getInstallTask().getStr("addon"));
    }

    /**
     * 安装菜单
     */
    private void installMenu() {
        JSONObject installTask = this.getInstallTask();
        coreMenuService.installAddonMenu(installTask.getStr("addon"));
        installTask.getJSONArray("step").add("installMenu");
        setInstallTask(installTask);
    }

    /**
     * 安装计划任务
     *
     */
    private void installSchedule() {
        JSONObject installTask = this.getInstallTask();
        coreScheduleService.installAddonSchedule(installTask.getStr("addon"));
        installTask.getJSONArray("step").add("installSchedule");
        setInstallTask(installTask);
    }

    /**
     * 安装数据库
     */
    private void installSql(String addon) {
        JSONObject installTask = this.getInstallTask();
        File installSql = new File(WebAppEnvs.get().webRootDownAddon + addon + "/sql/install.sql");
        if (installSql.exists()) {
            installTask.getJSONArray("step").add("installSql");
            setInstallTask(installTask);
            try {
                String sqlContent = FileUtils.readFileToString(installSql, "UTF-8");
                if (!sqlContent.isEmpty()) {
                    sqlContent = sqlContent.replace("{{prefix}}", GlobalConfig.tablePrefix);
                    SQLScriptRunnerTools.execScript(sqlContent);
                }
            } catch (Exception e) {
                throw new CommonException(e.getMessage());
            }
        }
    }

    /**
     * 安装数据库
     */
    private void uninstallSql(String addon) {
        File installSql = new File(WebAppEnvs.get().webRootDownAddon + addon + "/sql/uninstall.sql");
        if (installSql.exists()) {
            try {
                String sqlContent = FileUtils.readFileToString(installSql, "UTF-8");
                sqlContent = sqlContent.replace("{{prefix}}", GlobalConfig.tablePrefix);
                SQLScriptRunnerTools.execScript(sqlContent);
            } catch (Exception e) {
                throw new CommonException(e.getMessage());
            }
        }
    }

    /**
     * 卸载目录
     */
    private void uninstallDir() {
        (new AddonInstallTools()).uninstallDir(this.getInstallTask().getStr("addon"));
    }

    /**
     * 安装出现异常
     */
    private void installExceptionHandle() {
        JSONObject installTask = this.getInstallTask();
        String addon = installTask.getStr("addon");
        if (installTask.getJSONArray("step").indexOf("installDir") != -1) {
            this.uninstallDir();
        }
        if (installTask.getJSONArray("step").indexOf("installUniapp") != -1) {
            addonInstallTools.uninstallUniapp(addon);
        }
        if (installTask.getJSONArray("step").indexOf("installMenu") != -1) {
            coreMenuService.deleteMenu(addon, true);
        }
        if (installTask.getJSONArray("step").indexOf("installSql") != -1) {
            this.uninstallSql(addon);
        }
        if (installTask.getJSONArray("step").indexOf("installSchedule") != -1) {
            coreScheduleService.uninstallAddonSchedule(addon);
        }
        if (installTask.getStr("mode").equals("cloud")) cloudBuildService.clearBuildTask();
    }

    /**
     * 插件安装完成
     */
    private void handleAddonInstall() {
        JSONObject installTask = this.getInstallTask();
        // 安装菜单
        this.installMenu();
        // 执行安装sql
        this.installSql(installTask.getStr("addon"));
        // 安装计划任务
        this.installSchedule();

        JSONObject addonConfig = this.getAddonConfig(this.getInstallTask().getStr("addon"));
        Addon model = JSONUtil.toBean(addonConfig, Addon.class);
        model.setIcon("addon/" + model.getKey() + "/icon.png");
        coreAddonService.set(model);

        ArrayList<String> tips = new ArrayList<String>();

        String addon = installTask.getStr("addon");

        if (installTask.getStr("mode").equals("local")) {
            tips.add("本地安装成功后会将admin，web，wap端的插件代码进行安装，但是不会进行编译，请手动编译对应admin，web，wap端的代码");
            if (new File(WebAppEnvs.get().webRootDownAddon + addon + "/package/admin-package.json").exists())
                tips.add("该插件admin端引用了新的依赖需在项目根目录下admin目录执行 npm install 更新依赖");
            if (new File(WebAppEnvs.get().webRootDownAddon + addon + "/package/uni-app-package.json").exists())
                tips.add("该插件wap端引用了新的依赖需在项目根目录下uni-app目录执行 npm install 更新依赖");
            if (new File(WebAppEnvs.get().webRootDownAddon + addon + "/package/web-package.json").exists())
                tips.add("该插件web端引用了新的依赖需在项目根目录下web目录执行 npm install 更新依赖");
        }
        AddonInstallJavaTools.installExec(this, addon, tips);

        if (GlobalConfig.runActive.equalsIgnoreCase("dev")) {
            installTask.put("status", "success");
            installTask.put("tips", tips);

            cached.tag(CacheTagEnum.SITE_CACHE.getTagName()).clear();
        } else {
            installTask.put("status", "restart");
        }
        setInstallTask(installTask);
    }

    /**
     * 卸载插件
     *
     * @param addon
     */
    @Override
    public JSONObject uninstall(String addon) {
        if (!ObjectUtil.isNotEmpty(coreAddonService.getInfoByKey(addon))) {
            throw new CommonException("当前插件未安装,不能进行卸载操作");
        }

        // 卸载前先打包
        if (GlobalConfig.runActive.equalsIgnoreCase("dev")) {
            addonDevelopBuildService.build(addon);
        }

        try {
            // 卸载菜单
            coreMenuService.deleteMenu(addon, true);
            // 执行卸载sql
            this.uninstallSql(addon);
            // 处理uniapp
            addonInstallTools.uninstallUniapp(addon);
            // 移除定时任务
            coreScheduleService.uninstallAddonSchedule(addon);
            // 删除addon表插件
            addonMapper.delete(new QueryWrapper<Addon>().eq("`key`", addon));
            // 执行卸载其它
            AddonInstallJavaTools.uninstallExec(this, addon);
            // 是否需要重启服务
            JSONObject data = new JSONObject();
            if (!GlobalConfig.runActive.equalsIgnoreCase("dev")) {
                data.put("is_need_restart", 1);
            }
            cached.tag(CacheTagEnum.SITE_CACHE.getTagName()).clear();
            return data;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 插件卸载检测
     *
     * @param addon
     * @return
     */
    @Override
    public JSONObject uninstallCheck(String addon) {
        JSONObject checkResult = new JSONObject();
        checkResult.put("is_pass", true);
        return checkResult;
    }

    @Override
    public void installResult(boolean result, String addon, String message) {
        JSONObject installTask = this.getInstallTask();
        if (result) {
            installTask.put("status", "success");
            List<String> tips = AddonUpgradeHelper.getAddonInfo();
            installTask.put("tips", tips);
            setInstallTask(installTask);
            log.warn("安装成功...........................................");
        } else {
            installTask.put("status", "fail");
            installTask.put("failReason", "jar包加载错误.");
            List<String> tips = AddonUpgradeHelper.getAddonInfo();
            installTask.put("tips", tips);
            setInstallTask(installTask);
            log.warn("执行回滚操作<---------------------begin--------------------->");
            if (!GlobalConfig.runActive.equalsIgnoreCase("dev")) {
                // 生产环境进一步卸载
                this.rollback(addon);
            }
            log.warn("执行回滚操作<---------------------ended--------------------->");
        }
    }

    /**
     * 回滚
     * @param addon
     */
    private void rollback(String addon) {
        try {
            if (!ObjectUtil.isNotEmpty(coreAddonService.getInfoByKey(addon))) return;
            // 卸载菜单
            coreMenuService.deleteMenu(addon, true);
            // 执行卸载sql
            this.uninstallSql(addon);
            // 处理uniapp
            addonInstallTools.uninstallUniapp(addon);
            // 删除addon表插件
            addonMapper.delete(new QueryWrapper<Addon>().eq("`key`", addon));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取云安装日志
     *
     * @param addon
     * @return
     */
    public JSONObject cloudInstallLog(String addon) {
        JSONObject log = cloudBuildService.getBuildLog("install");
        if (log != null) {
            JSONArray data = log.getByPath("data.0", JSONArray.class);
            if (data.size() > 0) {
                JSONObject last = data.getJSONObject(data.size() - 1);
                if (last.getInt("code", 0).equals(0)) {
                    JSONObject installTask = getInstallTask();
                    installTask.put("status", "fail");
                    installTask.put("failReason", last.getStr("msg"));
                    setInstallTask(installTask);
                    cloudBuildService.clearBuildTask();
                    this.installExceptionHandle();
                    return log;
                }
                if (last.getInt("percent", 0).equals(100) && last.getStr("action", "").equals("build_success")) {
                    this.handleAddonInstall();
                }
            }
        }
        return log;
    }
}
