package com.niu.core.service.core.addon.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.file.AddonUpgradeHelper;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.component.context.WebAppEnvs;
import com.niu.core.config.GlobalConfig;
import com.niu.core.entity.addon.Addon;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.service.admin.niucloud.ICloudBuildService;
import com.niu.core.service.core.addon.*;
import com.niu.core.service.core.app.tools.SQLScriptRunnerTools;
import com.niu.core.service.core.sys.ICoreMenuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CoreAddonInstallServiceImpl extends CoreAddonBaseService implements ICoreAddonInstallService {

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

    private Boolean installCheck = false;

    private ArrayList<String> installStep = new ArrayList<String>();

    private JSONObject installTask = new JSONObject();

    /**
     * 插件安装检测
     *
     * @param addon
     * @return
     */
    @Override
    public JSONObject installCheck(String addon) {
        this.installCheck = true;

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
            if (!dir.getBool("status")) this.installCheck = false;
        }

        for (int i = 0; i < writeDir.size(); i++) {
            JSONObject dir = writeDir.getJSONObject(i);
            dir.set("status", new File(dir.getStr("dir")).canWrite());
            dir.set("dir", dir.getStr("dir").replace(rootPath, ""));
            writeDir.set(i, dir);
            if (!dir.getBool("status")) this.installCheck = false;
        }

        checkResult.put("is_pass", this.installCheck);
        checkResult.put("dir", new JSONObject().set("is_readable", readableDir).set("is_write", writeDir));
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
        if (!this.installCheck) {
            throw new CommonException("安装校验未通过");
        }
        Addon addonObject = coreAddonService.getInfoByKey(addon);
        if (ObjectUtil.isNotEmpty(addonObject)) {
            throw new CommonException("当前插件已安装,不能重复安装");
        }
        if (ObjectUtil.isNotEmpty(this.getInstallTask())) {
            throw new CommonException("有正在安装中的插件");
        }
        this.installTask.put("mode", mode);
        this.installTask.put("addon", addon);
        this.installTask.put("status", "installing");

        try {
            this.installDir();
            this.installDepend();
            this.installUniapp(addon);
            if (mode.equals("local")) {
                this.handleAddonInstall();
            } else {
                this.cloudInstall();
            }
            return installTask;
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
        if (this.installTask.isEmpty()) return null;
        return this.installTask;
    }

    /**
     * 取消安装任务
     */
    public void cancleInstall() {
        if (ObjectUtil.isNotEmpty(this.getInstallTask())) this.installTask.clear();
        this.installStep.clear();
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
        addonInstallTools.installDir(this.getInstallTask().getStr("addon"));
        this.installStep.add("installDir");
    }

    /**
     * 处理uniapp
     *
     * @param addon
     */
    private void installUniapp(String addon) {
        addonInstallTools.installUniapp(addon);
        this.installStep.add("installUniapp");
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
        coreMenuService.installAddonMenu(this.getInstallTask().getStr("addon"));
        this.installStep.add("installMenu");
    }

    /**
     * 安装数据库
     */
    private void installSql(String addon) {
        File installSql = new File(this.addonDownloadPath + "/" + addon + "/sql/install.sql");
        if (installSql.exists()) {
            this.installStep.add("installSql");
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
     * 安装数据库
     */
    private void uninstallSql(String addon) {
        File installSql = new File(this.addonDownloadPath + "/" + addon + "/sql/uninstall.sql");
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
        String addon = this.getInstallTask().getStr("addon");
        if (this.installStep.indexOf("installDir") != -1) {
            this.uninstallDir();
        }
        if (this.installStep.indexOf("installUniapp") != -1) {
            addonInstallTools.uninstallUniapp(addon);
        }
        if (this.installStep.indexOf("installMenu") != -1) {
            coreMenuService.deleteMenu(addon, true);
        }
        if (this.installStep.indexOf("installSql") != -1) {
            this.uninstallSql(addon);
        }
        this.installStep.clear();
        if (this.getInstallTask().getStr("mode").equals("cloud")) cloudBuildService.clearBuildTask();
    }

    /**
     * 插件安装完成
     */
    private void handleAddonInstall() {
        // 安装菜单
        this.installMenu();
        // 执行安装sql
        this.installSql(this.getInstallTask().getStr("addon"));
        JSONObject addonConfig = this.getAddonConfig(this.getInstallTask().getStr("addon"));
        coreAddonService.set(JSONUtil.toBean(addonConfig, Addon.class));

        ArrayList<String> tips = new ArrayList<String>();
        String addon = this.installTask.getStr("addon");

        if (this.installTask.getStr("mode").equals("local")) {
            tips.add("本地安装成功后会将admin，web，wap端的插件代码进行安装，但是不会进行编译，请手动编译对应admin，web，wap端的代码");
            if (new File(this.addonDownloadPath + "/" + addon + "/package/admin-package.json").exists())
                tips.add("该插件admin端引用了新的依赖需在项目根目录下admin目录执行 npm install 更新依赖");
            if (new File(this.addonDownloadPath + "/" + addon + "/package/uni-app-package.json").exists())
                tips.add("该插件wap端引用了新的依赖需在项目根目录下uni-app目录执行 npm install 更新依赖");
            if (new File(this.addonDownloadPath + "/" + addon + "/package/web-package.json").exists())
                tips.add("该插件web端引用了新的依赖需在项目根目录下web目录执行 npm install 更新依赖");
        }
        AddonInstallJavaTools.installExec(this, addon, tips);

        if (GlobalConfig.runActive.equalsIgnoreCase("dev")) {
            installTask.put("status", "success");
            installTask.put("tips", tips);
        } else {
            installTask.put("status", "restart");
        }
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
        try {
            // 卸载菜单
            coreMenuService.deleteMenu(addon, true);
            // 执行卸载sql
            this.uninstallSql(addon);
            // 处理uniapp
            addonInstallTools.uninstallUniapp(addon);
            // 删除addon表插件
            addonMapper.delete(new QueryWrapper<Addon>().eq("`key`", addon));
            // 执行卸载其它
            AddonInstallJavaTools.uninstallExec(this, addon);
            // 是否需要重启服务
            JSONObject data = new JSONObject();
            if (!GlobalConfig.runActive.equalsIgnoreCase("dev")) {
                data.put("is_need_restart", 1);
            }
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
        this.installTask = new JSONObject();
        this.installTask.put("mode", "local");
        this.installTask.put("addon", addon);
        if (result) {
            this.installTask.put("status", "success");
            List<String> tips = AddonUpgradeHelper.getAddonInfo();
            this.installTask.put("tips", tips);
            this.installCheck = false;
            log.warn("安装成功...........................................");
        } else {
            this.installTask.put("status", "fail");
            this.installTask.put("failReason", "jar包加载错误.");
            List<String> tips = AddonUpgradeHelper.getAddonInfo();
            this.installTask.put("tips", tips);
            log.warn("执行回滚操作<---------------------begin--------------------->");
            if (!GlobalConfig.runActive.equalsIgnoreCase("dev")) {
                // 生产环境进一步卸载
                this.rollback(addon);
                this.installCheck = false;
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
                    this.installTask.put("status", "fail");
                    this.installTask.put("failReason", last.getStr("msg"));
                    cloudBuildService.clearBuildTask();
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
