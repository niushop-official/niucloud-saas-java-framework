package com.niu.core.service.admin.upgrade.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.DatabaseBackup;
import com.niu.core.common.utils.NiucloudUtils;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.utils.file.PipeNameUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.enums.addon.AddonStatusEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.service.admin.niucloud.INiucloudService;
import com.niu.core.service.admin.upgrade.IUpgradeService;
import com.niu.core.service.admin.upgrade.vo.UpgradeTaskVo;
import com.niu.core.service.core.addon.AddonInstallTools;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.app.tools.SQLScriptRunnerTools;
import com.niu.core.service.core.schedule.ICoreScheduleService;
import com.niu.core.service.core.sys.ICoreMenuService;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UpgradeServiceImpl implements IUpgradeService {

    @Resource
    AddonMapper addonMapper;

    @Resource
    INiucloudService niucloudService;

    @Resource
    Environment env;

    @Resource
    ICoreMenuService coreMenuService;

    @Resource
    ICoreScheduleService coreScheduleService;

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    AddonInstallTools addonInstallTools;

    IUpgradeService upgradeService;

    @Resource
    public void setUpgradeService(@Lazy IUpgradeService upgradeService) {
        this.upgradeService = upgradeService;
    }

    /**
     * 获取升级内容
     *
     * @param addon
     * @return
     */
    @Override
    public JSONObject getUpgradeContent(String addon) {
        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("product_key", instance.getProductKey());

        if (addon.isEmpty()) {
            query.put("app_key", GlobalConfig.appKey);
            query.put("version", GlobalConfig.version);
        } else {
            Addon addonModel = addonMapper.selectOne(new QueryWrapper<Addon>().eq("`key`", addon).select("version"));
            query.put("app_key", addon);
            query.put("version", addonModel.getVersion());
        }

        return NiucloudUtils.Niucloud.get("member_app_upgrade/content", query);
    }

    /**
     * 升级
     *
     * @param addon
     * @return
     */
    @Override
    public UpgradeTaskVo upgrade(String addon) {
        if (getUpgradeTask() != null) throw new CommonException("已经有正在升级中的任务");

        NiucloudUtils instance = NiucloudUtils.getInstance();

        // 获取下载token
        Map<String, Object> actionQuery = new HashMap<>();
        actionQuery.put("data[product_key]", instance.getProductKey());
        actionQuery.put("data[framework_version]", GlobalConfig.version);
        if (addon.isEmpty()) {
            actionQuery.put("data[app_key]", GlobalConfig.appKey);
            actionQuery.put("data[version]", GlobalConfig.version);
        } else {
            Addon addonModel = addonMapper.selectOne(new QueryWrapper<Addon>().eq("`key`", addon).select("version"));
            actionQuery.put("data[app_key]", addon);
            actionQuery.put("data[version]", addonModel.getVersion());
        }
        JSONObject actionToken = niucloudService.getActionToken("upgrade", actionQuery);

        Map<String, Object> query = new HashMap<>();
        query.put("authorize_code", instance.getCode());
        query.put("token", actionToken == null ? "" : actionToken.getStr("token"));
        HttpResponse response = new NiucloudUtils.Cloud().build("cloud/upgrade").query(query).method(Method.GET).execute();

        if (response.getStatus() != 200) throw new CommonException("升级请求失败");

        JSONObject body = JSONUtil.parseObj(response.body());
        if (body.getInt("code").equals(0)) throw new RuntimeException(body.getStr("msg"));

        UpgradeTaskVo vo = new UpgradeTaskVo();
        vo.setUpgradeTime(DateUtil.now());
        vo.setAddon((String) actionQuery.get("data[app_key]"));
        vo.setKey(RandomUtil.randomString(10));
        vo.setUpgrade(actionQuery);
        vo.setStep("requestUpgrade");
        vo.getExecuted().add("requestUpgrade");
        vo.getLog().add(vo.getSteps().get("requestUpgrade").getTitle());
        vo.setParams(query);
        vo.setUpgradeContent(this.getUpgradeContent(addon).getJSONObject("data"));

        setUpgradeTaskCache(vo);

        return vo;
    }

    /**
     * 获取正在进行的升级任务
     * @return
     */
    @Override
    public UpgradeTaskVo getUpgradeTask() {
        Cached cache = CacheFactory.getCacheOperator();
        Object data = cache.get("upgrade");
        if (data == null) return null;
        return JSONUtil.toBean(JSONUtil.parseObj(data), UpgradeTaskVo.class);
    }

    private void setUpgradeTaskCache(UpgradeTaskVo vo) {
        Cached cache = CacheFactory.getCacheOperator();
        cache.put("upgrade", JSONUtil.parseObj(vo).toString());
    }

    /**
     * 清除升级任务
     */
    @Override
    public void clearUpgradeTask(int delayed) {
        if (delayed > 0) {
            try {
                Thread.sleep(delayed * 1000);
            } catch (Exception e) {
            }
        }
        Cached cache = CacheFactory.getCacheOperator();
        cache.remove("upgrade");
    }

    /**
     * 执行升级步骤
     *
     */
    @Override
    public void execute() {
        UpgradeTaskVo vo = this.getUpgradeTask();
        if (vo == null) return;

        List<String> steps = vo.getSteps().keySet().stream().collect(Collectors.toList());
        String step = steps.indexOf(vo.getStep()) < steps.size() - 1 ? steps.get(steps.indexOf(vo.getStep()) + 1) : "";
        step ="downloadFile";
        if (!step.isEmpty()) {
            vo.getExecuted().add(step);
            vo.getLog().add(vo.getSteps().get(step).getTitle());
            try {
                Map<String, Object> param = null;
                switch (step) {
                    case "downloadFile":
                        param = this.downloadFile(vo);
                        break;
                    case "backupCode":
                        this.backupCode(vo);
                        break;
                    case "backupSql":
                        this.backupSql(vo);
                        break;
                    case "coverCode":
                        this.coverCode(vo);
                        break;
                    case "handleUpgrade":
                        this.handleUpgrade(vo);
                        break;
                    case "handleVue":
                        this.handleVue(vo);
                        break;
                    case "refreshMenu":
                        this.refreshMenu(vo);
                        break;
                    case "installSchedule":
                        this.installSchedule(vo);
                        break;
                    case "upgradeComplete":
                        this.upgradeComplete(vo);
                        break;
                }
                if (param != null) {
                    vo.setParams(param);
                } else {
                    vo.setStep(step);
                    vo.setParams(null);
                }
                setUpgradeTaskCache(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * 获取升级文件目录
     * @param vo
     * @return
     */
    private String upgradeDir(UpgradeTaskVo vo) {
        return WebAppEnvs.get().webRootDownRuntime + "upgrade/" + vo.getAddon() + "/" + vo.getUpgrade().get("data[version]") + "-" + vo.getUpgradeContent().getStr("upgrade_version");
    }

    /**
     * 下载升级文件
     *
     * @param vo
     * @return
     */
    private Map<String, Object> downloadFile(UpgradeTaskVo vo) throws Exception
    {
        Map<String, Object> param = vo.getParams();

        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("authorize_code", instance.getCode());
        query.put("token", param.get("token"));

        File downloadDir = new File(upgradeDir(vo) + "/download/");
        Integer chunkSize = 1 * 1024 * 1024;

        if (param.get("index") == null) {
            FileTools.createDirs(downloadDir.getPath());

            HttpResponse response = new NiucloudUtils.Cloud().build("cloud/upgrade/download").query(query).header("Range", "bytes=0-").method(Method.HEAD).execute();
            String length = response.header("Content-Length");
            Double step = Math.ceil(Double.valueOf(length) / chunkSize);

            param.put("index", 0);
            param.put("step", step);
            param.put("length", length);

            return param;
        } else {
            Long index = Long.valueOf((Integer) param.get("index"));
            Long step = Long.valueOf((Integer) param.get("step"));
            Long length = Long.parseLong((String) param.get("length"));

            File file = new File(downloadDir.getPath(), "download.zip");
            FileOutputStream fos = new FileOutputStream(file, true);

            if (index < step) {
                Long start = index * chunkSize;
                Long end = (index + 1) * chunkSize;
                if (end > length) end = length;

                HttpResponse response = new NiucloudUtils.Cloud().build("cloud/upgrade/download").query(query).header("Range", "bytes=" + start + "-" + end).method(Method.GET).execute();
                fos.write(response.bodyBytes());

                param.put("index", index + 1);
                return param;
            } else {
                ZipUtil.unzip(file.getPath(), downloadDir.getPath());
                file.deleteOnExit();
                return null;
            }
        }
    }

    /**
     * 备份源码
     *
     * @param vo
     */
    private void backupCode(UpgradeTaskVo vo) throws Exception {
        File backupDir = new File(upgradeDir(vo) + "/backup/code/");
        FileTools.createDirs(backupDir.getPath());

        WebAppEnvs envs = WebAppEnvs.get();

        // 备份前端
        FileUtils.copyDirectory(new File(WebAppEnvs.get().webRootDownPublic), new File(backupDir, "webroot/public"));
        // 备份前端源码
        String[] adminExclusionDir = {"node_modules", ".vscode", "dist", ".idea"};
        if (new File(envs.projectRoot + "/admin").exists()) {
            FileUtils.copyDirectory(new File(envs.projectRoot + "/admin"), new File(backupDir, "admin"), FileTools.createExclusionFilter(adminExclusionDir));
        }
        if (new File(envs.webRootDownRuntime + "/admin").exists()) {
            FileUtils.copyDirectory(new File(envs.webRootDownRuntime + "/admin"), new File(backupDir, "webroot/runtime/admin"), FileTools.createExclusionFilter(adminExclusionDir));
        }
        String[] uniappExclusionDir = {"node_modules", "unpackage", "dist"};
        if (new File(envs.projectRoot + "/uni-app").exists()) {
            FileUtils.copyDirectory(new File(envs.projectRoot + "/uni-app"), new File(backupDir, "uni-app"), FileTools.createExclusionFilter(uniappExclusionDir));
        }
        if (new File(envs.webRootDownRuntime + "/uni-app").exists()) {
            FileUtils.copyDirectory(new File(envs.webRootDownRuntime + "/uni-app"), new File(backupDir, "webroot/runtime/uni-app"), FileTools.createExclusionFilter(uniappExclusionDir));
        }
        String[] webExclusionDir = {"node_modules", ".output", "nuxt", "dist", ".nuxt"};
        if (new File(envs.projectRoot + "/web").exists()) {
            FileUtils.copyDirectory(new File(envs.projectRoot + "/web"), new File(backupDir, "web"), FileTools.createExclusionFilter(webExclusionDir));
        }
        if (new File(envs.webRootDownRuntime + "/web").exists()) {
            FileUtils.copyDirectory(new File(envs.webRootDownRuntime + "/web"), new File(backupDir, "webroot/runtime/web"), FileTools.createExclusionFilter(webExclusionDir));
        }

        if (vo.getAddon().equals(GlobalConfig.appKey)) {
            // 备份java源码
            String[] addonExclusionDir = {".mvm", ".mvn", "target"};
            File javaFile = new File(envs.projectRoot + "/niucloud-core");
            if (javaFile.exists()) FileUtils.copyDirectory(javaFile, new File(backupDir, "niucloud-core"), FileTools.createExclusionFilter(addonExclusionDir));
            // 备份jar文件
            FileUtils.copyFile(new File(envs.webRootDownJar + "/web-app-exec.jar"), new File(backupDir, "webroot/jar/web-app-exec.jar"));
        } else {
            // 备份java源码
            String[] addonExclusionDir = {".mvm", ".mvn", "target"};
            File javaFile = new File(envs.projectNiucloudAddon + vo.getAddon());
            if (javaFile.exists()) FileUtils.copyDirectory(javaFile, new File(backupDir, "niucloud-addon/" + vo.getAddon()), FileTools.createExclusionFilter(addonExclusionDir));
            // 备份jar文件
            File jarFile = new File(envs.webRootDownJar + "/addon/"+ vo.getAddon() +".jar");
            if (jarFile.exists()) FileUtils.copyFile(jarFile, new File(backupDir, "webroot/jar/addon/"+ vo.getAddon() +".jar"));
            // 备份webroot下的源码
            File sourceFile = new File(envs.webRootDownAddon + vo.getAddon());
            if (sourceFile.exists()) FileUtils.copyDirectory(sourceFile, new File(backupDir, "webroot/addon/" + vo.getAddon()));
        }
    }

    /**
     * 备份数据库
     * @param vo
     */
    private void backupSql(UpgradeTaskVo vo) throws Exception {
        File backupDir = new File(upgradeDir(vo) + "/backup/sql/");
        FileTools.createDirs(backupDir.getPath());

        String url = env.getProperty("spring.datasource.url");
        String user = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");

        // 要排除的表
        String[] excludeTables = {
                "?_sys_area".replace("?_", GlobalConfig.tablePrefix),
                "?_sys_user_log".replace("?_", GlobalConfig.tablePrefix),
                "?_jobs".replace("?_", GlobalConfig.tablePrefix),
                "?_jobs_failed".replace("?_", GlobalConfig.tablePrefix)
        };
        (new DatabaseBackup(url, user, password)).setDir(backupDir.getPath() + "/").setFilter(excludeTables).backup();
    }

    /**
     * 合并更新文件
     * @param vo
     */
    private void coverCode(UpgradeTaskVo vo) throws Exception {
        if (WebAppEnvs.get().envType.equals("dev")) {
            String upgradeVersion = vo.getUpgradeContent().getStr("upgrade_version");
            File changeRecord = new File(upgradeDir(vo) + "/download/code/" + upgradeVersion + ".txt");

            File sourceFile = null;
            File projectFile = null;

            if (vo.getAddon().equals(GlobalConfig.appKey)) {
                sourceFile = new File(WebAppEnvs.get().projectRoot);
            } else {
                sourceFile = new File(WebAppEnvs.get().webRootDownAddon + vo.getAddon());
                projectFile = new File(WebAppEnvs.get().projectNiucloudAddon + vo.getAddon());
            }

            // 移除已删除文件
            if (changeRecord.exists()) {
                List<String> change =  Arrays.asList(FileUtils.readFileToString(changeRecord, "UTF-8").split("\n"));
                for (String line : change) {
                    String[] content = line.split(" ");
                    if (content[0].equals("-")) {
                        (new File(sourceFile, content[2])).deleteOnExit();
                        if (projectFile != null && content[2].startsWith("/java/") && projectFile.exists()) (new File(projectFile, content[2].replaceFirst("^/java/", ""))).deleteOnExit();
                    }
                }
            }

            // 覆盖变更文件
            if (vo.getAddon().equals(GlobalConfig.appKey)) {
                FileUtils.copyDirectory(new File(upgradeDir(vo) + "/download/code/"), sourceFile);
            } else {
                FileUtils.copyDirectory(new File(upgradeDir(vo) + "/download/code/"), sourceFile);
                if (projectFile != null && projectFile.exists() && new File(upgradeDir(vo) + "/download/code/java/").exists()) {
                    FileUtils.copyDirectory(new File(upgradeDir(vo) + "/download/code/java/"), projectFile);
                }
            }
        } else {
            PipeNameUtils.noticeBootRestart(GlobalConfig.applicationName, vo.getAddon().equals(GlobalConfig.appKey) ? "core" : vo.getAddon());
            Thread.sleep(3000);
        }
    }

    /**
     * 处理升级方法
     * @param vo
     */
    private void handleUpgrade(UpgradeTaskVo vo) {
        JSONArray versionList = vo.getUpgradeContent().getJSONArray("version_list");
        String addon = vo.getAddon().equals(GlobalConfig.appKey) ? "core" : vo.getAddon();
        for (int i = 0; i < versionList.size(); i++) {
            String versionNo = versionList.getJSONObject(i).getStr("version_no");

            String sql = AddonModuleContext.readResourceAsStreamToText(addon, "upgrade/" + versionNo + ".sql");
            if (!sql.isEmpty()) {
                sql = sql.replace("{{prefix}}", GlobalConfig.tablePrefix);
                try {
                    SQLScriptRunnerTools.execScript(sql);
                } catch (Exception e) {
                    throw new CommonException(versionNo + "版本升级sql执行出错，错误原因：" + e.getMessage());
                }
            }

            try {
                try {
                    String className = "com.niu."+ addon +".upgrade.V" + versionNo.replaceAll(".", "") + ".Upgrade";
                    Class<?> clazz = Class.forName(className);
                    try {
                        Object instance = clazz.getDeclaredConstructor().newInstance();
                        java.lang.reflect.Method method = clazz.getMethod("handle");
                        method.invoke(instance);
                    } catch (NoSuchMethodException e) {
                    }
                } catch (ClassNotFoundException e) {
                }
            } catch (Exception e) {
                throw new CommonException(versionNo + "版本升级方法执行出错，错误原因：" + e.getMessage());
            }
        }
    }

    /**
     * 处理vue
     * @param vo
     */
    private void handleVue(UpgradeTaskVo vo) throws Exception {
        WebAppEnvs envs = WebAppEnvs.get();

        if (!vo.getAddon().equals(GlobalConfig.appKey)) {
            File sourceDir = new File(envs.webRootDownAddon + vo.getAddon());
            if (sourceDir.exists()) {
                addonInstallTools.installVue(vo.getAddon());
            }
        }

        List<String> addons = addonMapper.selectList(new MPJQueryWrapper<Addon>()
                .select("`key`")
                .eq("status", AddonStatusEnum.ON.getCode()))
                .stream().map(i -> i.getKey()).collect(Collectors.toList())
        ;

        // 处理pages.json
        if (new File(envs.webRoot + "uni-app/").exists()) addonInstallTools.handlePagesJson(envs.webRoot + "/uni-app/", addons);
        if (new File(envs.webRootDownRuntime + "uni-app/").exists()) addonInstallTools.handlePagesJson(envs.webRootDownRuntime + "/uni-app/", addons);

        // 处理组件
        if (new File(envs.webRoot + "uni-app/").exists()) addonInstallTools.handleUniappComponent(envs.webRoot + "/uni-app/", addons);
        if (new File(envs.webRootDownRuntime + "uni-app/").exists()) addonInstallTools.handleUniappComponent(envs.webRootDownRuntime + "/uni-app/", addons);

        // 处理语言包
        for (String addon: addons) {
            addonInstallTools.setAddon(addon);
            if (new File(envs.webRoot + "uni-app/").exists()) addonInstallTools.mergeUniappLocale(envs.webRoot + "/uni-app/", "install");
            if (new File(envs.webRootDownRuntime + "uni-app/").exists()) addonInstallTools.mergeUniappLocale(envs.webRootDownRuntime + "/uni-app/", "install");

            addonInstallTools.installDepend(addon);
        }
    }

    /**
     * 刷新菜单
     * @param vo
     */
    private void refreshMenu(UpgradeTaskVo vo) {
        if (vo.getAddon().equals(GlobalConfig.appKey)) {
            coreMenuService.refreshAddonMenu("core");
        } else {
            coreMenuService.refreshAddonMenu(vo.getAddon());
        }
    }

    /**
     * 安装计划任务
     * @param vo
     */
    private void installSchedule(UpgradeTaskVo vo) {
        if (vo.getAddon().equals(GlobalConfig.appKey)) {
            coreScheduleService.installSystemSchedule();
        } else {
            coreScheduleService.installAddonSchedule(vo.getAddon());
        }
    }

    private void upgradeComplete(UpgradeTaskVo vo) {
        vo.setStatus("success");
        if (!vo.getAddon().equals(GlobalConfig.appKey)) {
            Addon addon = JSONUtil.toBean(coreAddonService.getAddonConfig(vo.getAddon()), Addon.class);
            coreAddonService.set(addon);
        }
        File backupDir = new File(upgradeDir(vo));
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(vo)), new File(backupDir, "log.json"));

        upgradeService.clearUpgradeTask(5);
    }
}
