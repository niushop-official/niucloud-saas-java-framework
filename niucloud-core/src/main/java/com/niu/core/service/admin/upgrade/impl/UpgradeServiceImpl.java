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
import com.niu.core.common.component.upgrade.factory.UpgradeProviderFactory;
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
import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
     * 升级检测
     *
     * @param addon
     * @return
     */
    @Override
    public JSONObject upgradeCheck(String addon) {
        boolean upgradeCheck = true;

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
            if (!dir.getBool("status")) upgradeCheck = false;
        }

        for (int i = 0; i < writeDir.size(); i++) {
            JSONObject dir = writeDir.getJSONObject(i);
            dir.set("status", new File(dir.getStr("dir")).canWrite());
            dir.set("dir", dir.getStr("dir").replace(rootPath, ""));
            writeDir.set(i, dir);
            if (!dir.getBool("status")) upgradeCheck = false;
        }

        checkResult.put("is_pass", upgradeCheck);
        checkResult.put("dir", new JSONObject().set("is_readable", readableDir).set("is_write", writeDir));
        return checkResult;
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

        return NiucloudUtils.Niucloud.get("member_app_upgrade/content", query).getJSONObject("data");
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
        vo.setEnvType(WebAppEnvs.get().envType);
        vo.setUpgradeTime(DateUtil.now());
        vo.setAddon((String) actionQuery.get("data[app_key]"));
        vo.setKey(RandomUtil.randomString(10));
        vo.setUpgrade(actionQuery);
        vo.setStep("requestUpgrade");
        vo.getExecuted().add("requestUpgrade");
        vo.getLog().add(vo.getSteps().get("requestUpgrade").getTitle());
        vo.setParams(query);
        vo.setUpgradeContent(this.getUpgradeContent(addon));
        vo.setVersion((String) actionQuery.get("data[version]"));
        vo.setUpgradeVersion(vo.getUpgradeContent().getStr("upgrade_version"));
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

    @Override
    public void setUpgradeTaskCache(UpgradeTaskVo vo) {
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

        if (ObjectUtil.isNotEmpty(vo.getStatus()) && vo.getStatus().equals("restarting")) return;

        List<String> steps = vo.getSteps().keySet().stream().collect(Collectors.toList());
        String step = steps.indexOf(vo.getStep()) < steps.size() - 1 ? steps.get(steps.indexOf(vo.getStep()) + 1) : "";

        if (!step.isEmpty()) {
            if (!vo.getExecuted().contains(step)) {
                vo.getExecuted().add(step);
                vo.getLog().add(vo.getSteps().get(step).getTitle());
            }
            try {
                Map<String, Object> param = null;
                param = (Map<String, Object>) dynamicMethodCall(step, vo);
                if (param != null) {
                    vo.setParams(param);
                } else {
                    vo.setStep(step);
                    vo.setParams(null);
                }
                setUpgradeTaskCache(vo);
            } catch (Exception e) {
                vo.setStep(step);
                vo.getError().add(e.getMessage());
                setUpgradeTaskCache(vo);
                this.upgradeErrorHandle(vo);
                System.out.println("升级异常.");
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
        return WebAppEnvs.get().webRootDownRuntime + "upgrade/" + vo.getAddon() + "/" + vo.getVersion() + "-" + vo.getUpgradeVersion();
    }

    /**
     * 下载升级文件
     *
     * @param vo
     * @return
     */
    public Map<String, Object> downloadFile(UpgradeTaskVo vo) throws Exception
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
            FileUtils.cleanDirectory(downloadDir);

            HttpResponse response = new NiucloudUtils.Cloud().build("cloud/upgrade/download").query(query).header("Range", "bytes=0-").method(Method.HEAD).execute();
            String totalLength = response.header("Content-range");
            String length = ObjectUtil.defaultIfNull(totalLength.split("/")[1], "");
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
                fos.close();
                ZipUtil.unzip(file.getPath(), downloadDir.getPath());
                Thread.sleep(1000);
                file.delete();
                return null;
            }
        }
    }

    /**
     * 备份源码
     *
     * @param vo
     */
    public void backupCode(UpgradeTaskVo vo) throws Exception {
        File backupDir = new File(upgradeDir(vo) + "/backup/code/");
        FileTools.createDirs(backupDir.getPath());
        FileUtils.cleanDirectory(backupDir);

        WebAppEnvs envs = WebAppEnvs.get();

        // 备份前端
        FileUtils.copyDirectory(new File(envs.webRootDownPublic), new File(backupDir, "webroot/public"));

        String[] uniAppExclusionDir = {"node_modules", "unpackage", "dist"};
        if (new File(envs.projectRoot, "uni-app").exists()) {
            FileUtils.copyDirectory(new File(envs.projectRoot, "uni-app"), new File(backupDir, "webroot/runtime/uni-app"), FileTools.createExclusionFilter(uniAppExclusionDir));
        }
        if (new File(envs.webRootDownRuntime + "uni-app").exists()) {
            FileUtils.copyDirectory(new File(envs.webRootDownRuntime + "uni-app"), new File(backupDir, "webroot/runtime/uni-app"), FileTools.createExclusionFilter(uniAppExclusionDir));
        }

        String[] adminExclusionDir = {"node_modules", ".vscode", "dist", ".idea"};
        if (new File(envs.projectRoot, "admin").exists()) {
            FileUtils.copyDirectory(new File(envs.projectRoot, "admin"), new File(backupDir, "webroot/runtime/admin"), FileTools.createExclusionFilter(adminExclusionDir));
        }
        if (new File(envs.webRootDownRuntime + "admin").exists()) {
            FileUtils.copyDirectory(new File(envs.webRootDownRuntime + "admin"), new File(backupDir, "webroot/runtime/admin"), FileTools.createExclusionFilter(adminExclusionDir));
        }

        String[] webExclusionDir = {"node_modules", ".output", "nuxt", "dist", ".nuxt"};
        if (new File(envs.projectRoot, "web").exists()) {
            FileUtils.copyDirectory(new File(envs.projectRoot, "web"), new File(backupDir, "webroot/runtime/web"), FileTools.createExclusionFilter(webExclusionDir));
        }
        if (new File(envs.webRootDownRuntime + "web").exists()) {
            FileUtils.copyDirectory(new File(envs.webRootDownRuntime + "web"), new File(backupDir, "webroot/runtime/web"), FileTools.createExclusionFilter(webExclusionDir));
        }

        File srcDir = null;
        File destDir = null;
        if (vo.getAddon().equals(GlobalConfig.appKey)) {
            srcDir = new File(envs.projectRoot);
            destDir = backupDir;
        } else {
            srcDir = new File(envs.webRootDownAddon + vo.getAddon());
            destDir = new File(backupDir, "webroot/addon/" + vo.getAddon());

            // 备份jar文件
            File jarFile = new File(envs.webRootDownJar, "addon/" + vo.getAddon() + ".jar");
            if (jarFile.exists()) FileUtils.copyFile(jarFile, new File(backupDir, "webroot/jar/addon/" + vo.getAddon() + ".jar"));
        }

        JSONArray versionList = vo.getUpgradeContent().getJSONArray("version_list");
        for (int i = 0; i < versionList.size(); i++) {
            String versionNo = versionList.getJSONObject(i).getStr("version_no");

            File changeRecord = new File(upgradeDir(vo) + "/download/" + versionNo + ".txt");

            if (changeRecord.exists()) {
                List<String> change = Arrays.asList(FileUtils.readFileToString(changeRecord, "UTF-8").split("\n"));
                for (String line : change) {
                    String[] content = line.split(" ");
                    if (!content[0].equals("+")) {
                        String path = content[2];
                        File changeFile = new File(srcDir, path);
                        if (changeFile.exists()) FileUtils.copyFile(changeFile, new File(destDir, path));
                    }
                }
            }
        }
    }

    /**
     * 备份数据库
     * @param vo
     */
    public void backupSql(UpgradeTaskVo vo) throws Exception {
        File backupDir = new File(upgradeDir(vo) + "/backup/sql/");
        FileTools.createDirs(backupDir.getPath());
        FileUtils.cleanDirectory(backupDir);

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
    public void coverCode(UpgradeTaskVo vo) throws Exception {
        if (WebAppEnvs.get().envType.equals("dev")) {

            File sourceFile = null;
            File projectFile = null;

            if (vo.getAddon().equals(GlobalConfig.appKey)) {
                sourceFile = new File(WebAppEnvs.get().projectRoot);
            } else {
                sourceFile = new File(WebAppEnvs.get().webRootDownAddon + vo.getAddon());
                projectFile = new File(WebAppEnvs.get().projectNiucloudAddon + vo.getAddon());
            }

            JSONArray versionList = vo.getUpgradeContent().getJSONArray("version_list");
            for (int i = 0; i < versionList.size(); i++) {
                String versionNo = versionList.getJSONObject(i).getStr("version_no");

                File changeRecord = new File(upgradeDir(vo) + "/download/" + versionNo + ".txt");
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

                IOFileFilter envFile = FileTools.createExclusionFilter(new String[]{
                    "application-prod.yml",
                    ".env.development",
                    ".env.production",
                    ".env.dev",
                    ".env.product"
                });

                // 覆盖变更文件
                File codeDir = new File(upgradeDir(vo) + "/download/" + versionNo);
                if (vo.getAddon().equals(GlobalConfig.appKey)) {
                    File[] childFile = codeDir.listFiles();
                    for (File file : childFile) {
                        if (file.isDirectory()) {
                            if (new File(sourceFile, file.getName()).exists()) FileUtils.copyDirectory(file, new File(sourceFile, file.getName()), envFile);
                        } else {
                            FileUtils.copyFile(file, new File(sourceFile, file.getName()));
                        }
                    }
                } else {
                    FileUtils.copyDirectory(codeDir, sourceFile);
                    if (projectFile != null && projectFile.exists() && new File(codeDir, "java").exists()) {
                        FileUtils.copyDirectory(new File(codeDir, "java"), projectFile);
                    }
                }
            }

            vo.setStatus("restarting");
            setUpgradeTaskCache(vo);
        } else {
            vo.setStep("coverCode");
            vo.setStatus("restarting");
            setUpgradeTaskCache(vo);

            String dir = vo.getUpgrade().get("data[version]") + "-" + vo.getUpgradeContent().getStr("upgrade_version");

            List<String> versions = new LinkedList<>();
            JSONArray versionList = vo.getUpgradeContent().getJSONArray("version_list");
            for (int i = 0; i < versionList.size(); i++) {
                versions.add(versionList.getJSONObject(i).getStr("version_no"));
            }

            PipeNameUtils.noticeBootRestartByUpgrade(GlobalConfig.applicationName, vo.getAddon(), dir, String.join("-", versions));
            Thread.sleep(3000);
        }
    }

    /**
     * 处理升级方法
     * @param vo
     */
    public void handleUpgrade(UpgradeTaskVo vo) {
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
                UpgradeProviderFactory.handle(addon, versionNo);
            } catch (Exception e) {
                throw new CommonException(versionNo + "版本升级方法执行出错，错误原因：" + e.getMessage());
            }
        }
    }

    /**
     * 处理vue
     * @param vo
     */
    public void handleVue(UpgradeTaskVo vo) throws Exception {
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
    public void refreshMenu(UpgradeTaskVo vo) {
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
    public void installSchedule(UpgradeTaskVo vo) {
        if (vo.getAddon().equals(GlobalConfig.appKey)) {
            coreScheduleService.installSystemSchedule();
        } else {
            coreScheduleService.installAddonSchedule(vo.getAddon());
        }
    }

    public void upgradeComplete(UpgradeTaskVo vo) {
        vo.setStatus("success");
        if (!vo.getAddon().equals(GlobalConfig.appKey)) {
            Addon addon = JSONUtil.toBean(coreAddonService.getAddonConfig(vo.getAddon()), Addon.class);
            coreAddonService.set(addon);
        }
        File backupDir = new File(upgradeDir(vo));
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(vo)), new File(backupDir, DateUtil.now() + ".log"));

        upgradeService.clearUpgradeTask(5);
    }

    /**
     * 升级异常处理
     * @param vo
     */
    public void upgradeErrorHandle(UpgradeTaskVo vo) {
        File backupDir = new File(upgradeDir(vo));
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(vo)), new File(backupDir, DateUtil.now() + ".log"));

        Map<String, UpgradeTaskVo.Step> steps = new LinkedHashMap<>();
        steps.put("rollback", new UpgradeTaskVo.Step("rollback", "升级失败开始回滚"));

        if (vo.getExecuted().contains("coverCode")) {
            steps.put("restoreCover", new UpgradeTaskVo.Step("restoreCover", "恢复变更文件"));
        }
        if (vo.getExecuted().contains("handleUpgrade")) {
            steps.put("restoreDatabase", new UpgradeTaskVo.Step("restoreDatabase", "恢复数据库"));
        }
        steps.put("rollbackComplete", new UpgradeTaskVo.Step("rollbackComplete", "回滚完成"));

        vo.setSteps(steps);
        vo.setStep("rollback");
        vo.getLog().add(steps.get("rollback").getTitle());
        vo.getExecuted().add("rollback");

        setUpgradeTaskCache(vo);
    }

    /**
     * 恢复文件备份
     */
    public void restoreCover(UpgradeTaskVo vo) {
        if (WebAppEnvs.get().envType.equals("dev")) {
            try {
                File backupDir = new File(upgradeDir(vo) + "/backup/code/");
                WebAppEnvs envs = WebAppEnvs.get();

                File rootDir = null;
                if (vo.getAddon().equals(GlobalConfig.appKey)) {
                    rootDir = new File(envs.projectRoot);
                } else {
                    rootDir = new File(envs.webRootDownAddon + vo.getAddon());
                }
                FileUtils.copyDirectory(backupDir, new File(envs.projectRoot));

                JSONArray versionList = vo.getUpgradeContent().getJSONArray("version_list");
                for (int i = 0; i < versionList.size(); i++) {
                    String versionNo = versionList.getJSONObject(i).getStr("version_no");
                    File changeRecord = new File(upgradeDir(vo) + "/download/" + versionNo + ".txt");

                    // 移除更新新增的文件
                    if (changeRecord.exists()) {
                        List<String> change = Arrays.asList(FileUtils.readFileToString(changeRecord, "UTF-8").split("\n"));
                        for (String line : change) {
                            String[] content = line.split(" ");
                            if (content[0].equals("+")) {
                                String path = content[2];
                                File changeFile = new File(rootDir, path);
                                if (changeFile.exists()) changeFile.delete();
                            }
                        }
                    }
                }

                if (!vo.getAddon().equals(GlobalConfig.appKey)) {
                    File sourceDir = new File(envs.webRootDownAddon + vo.getAddon() + "/java");
                    File projectDir = new File(envs.projectNiucloudAddon + vo.getAddon());
                    if (sourceDir.exists() && projectDir.exists()) FileUtils.copyDirectory(sourceDir, projectDir);
                }
            } catch (Exception e) {
                vo.getError().add("变更文件恢复失败，错误信息：" + e.getMessage());
            }
        } else {
            try {
                vo.setStep("restoreCover");
                vo.setStatus("restarting");
                setUpgradeTaskCache(vo);

                String dir = vo.getUpgrade().get("data[version]") + "-" + vo.getUpgradeContent().getStr("upgrade_version");
                PipeNameUtils.noticeBootRestartByUpgradeRollback(GlobalConfig.applicationName, vo.getAddon(), dir);
                Thread.sleep(3000);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 恢复数据库
     */
    public void restoreDatabase(UpgradeTaskVo vo) {
        try {
            File backupDir = new File(upgradeDir(vo), "/backup/sql/");
            File[] files = backupDir.listFiles();
            Arrays.sort(files);

            for (File file : files) {
                if (file.isFile()) {
                    SQLScriptRunnerTools.execScript(file);
                }
            }
        } catch (Exception e) {
            vo.getError().add("数据库回滚失败，错误信息：" + e.getMessage());
            String dir = "webboot/upgrade/" + vo.getAddon() + "/" + vo.getVersion() + "-" + vo.getUpgradeVersion() + "/backup/sql";
            vo.getError().add("数据库回滚失败请找到" + dir + "目录下的数据库备份文件手动进行恢复");
        }
    }

    /**
     * 回滚完成
     * @param vo
     */
    public void rollbackComplete(UpgradeTaskVo vo) {
        vo.setStatus("rollback");
        File backupDir = new File(upgradeDir(vo));
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(vo)), new File(backupDir, DateUtil.now() + ".log"));
        upgradeService.clearUpgradeTask(5);
    }

    public Object dynamicMethodCall(String methodName, Object... args) {
        try {
            // 获取当前类的 Class 对象
            Class<?> clazz = this.getClass();
            // 获取方法对象
            java.lang.reflect.Method method = clazz.getMethod(methodName, extractParameterTypes(args));
            // 调用方法
            System.out.println("dynamicMethodCall method：" + methodName);
            Object result = method.invoke(this, args);
            if (method.getReturnType() == void.class) {
                return null;
            } else {
                return result;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            if  (e instanceof InvocationTargetException) {
                ((InvocationTargetException) e).getCause().printStackTrace();
            } else {
                e.printStackTrace();
            }
            return null;
        }
    }

    private Class<?>[] extractParameterTypes(Object[] args) {
        if (args == null || args.length == 0) {
            return new Class<?>[0];
        }
        Class<?>[] parameterTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        return parameterTypes;
    }
}
