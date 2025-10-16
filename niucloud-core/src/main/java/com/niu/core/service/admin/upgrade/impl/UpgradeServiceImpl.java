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
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import com.niu.core.entity.site.Site;
import com.niu.core.entity.sys.SysBackupRecords;
import com.niu.core.entity.sys.SysUpgradeRecords;
import com.niu.core.enums.addon.AddonStatusEnum;
import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.enums.sys.UpgradeRecordStatusEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysBackupRecordsMapper;
import com.niu.core.service.admin.niucloud.ICloudBuildService;
import com.niu.core.service.admin.niucloud.INiucloudService;
import com.niu.core.service.admin.sys.ISysUpgradeRecordsService;
import com.niu.core.service.admin.sys.param.SysUpgradeRecordsParam;
import com.niu.core.service.admin.upgrade.IUpgradeService;
import com.niu.core.service.admin.upgrade.param.UpgradeParam;
import com.niu.core.service.admin.upgrade.vo.UpgradeContentVo;
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

import jakarta.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
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

    @Resource
    SysBackupRecordsMapper sysBackupRecordsMapper;

    @Resource
    ISysUpgradeRecordsService sysUpgradeRecordsService;

    @Resource
    ICloudBuildService cloudBuildService;

    @Resource
    SiteMapper siteMapper;

    /**
     * 升级检测
     *
     * @param addon
     * @return
     */
    @Override
    public JSONObject upgradeCheck(String addon) {
        if (GlobalConfig.runActive.equals("dev")) throw new CommonException("开发环境下请先关闭服务启动webroot/jar下的web-app-boot-exec进行升级");

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
    public UpgradeContentVo getUpgradeContent(String addon) {
        NiucloudUtils instance = NiucloudUtils.getInstance();
        UpgradeContentVo vo = new UpgradeContentVo();

        LinkedList<Map> apps = new LinkedList<>();

        if (addon.isEmpty()) {
            Map<String, Object> query = new HashMap<>();
            query.put("product_key", instance.getProductKey());
            query.put("app_key", GlobalConfig.appKey);
            query.put("version", GlobalConfig.version);
            apps.add(query);
        } else {
            for (String key : addon.split(",")) {
                Addon addonModel = addonMapper.selectOne(new QueryWrapper<Addon>().eq("`key`", key).select("version,type"));
                Map<String, Object> query = new HashMap<>();
                query.put("product_key", instance.getProductKey());
                query.put("app_key", key);
                query.put("version", addonModel.getVersion());
                if (addonModel.getType().equals(AddonTypeEnum.APP.getType())) {
                    apps.addFirst(query);
                } else {
                    apps.add(query);
                }
            }
        }

        for (Map<String, Object> item: apps) {
            JSONObject content = NiucloudUtils.Niucloud.get("member_app_upgrade/content", item).getJSONObject("data");
            if (content != null) {
                UpgradeContentVo.Content contentVo = JSONUtil.toBean(content, UpgradeContentVo.Content.class);
                vo.getContent().add(contentVo);
                vo.getUpgradeApps().add(contentVo.getApp().getAppKey());
            }
        }

        vo.setLastBackup(sysBackupRecordsMapper.selectOne(new QueryWrapper<SysBackupRecords>()
            .eq("status", "complete")
            .orderByDesc("complete_time")
            .last("limit 1")
        ));

        return vo;
    }

    /**
     * 升级
     *
     * @param
     * @return
     */
    @Override
    public UpgradeTaskVo upgrade(UpgradeParam upgradeParam) {
        if (GlobalConfig.runActive.equals("dev")) throw new CommonException("开发环境下请先关闭服务启动webroot/jar下的web-app-boot-exec进行升级");
        if (getUpgradeTask() != null) throw new CommonException("已经有正在升级中的任务");

        UpgradeContentVo upgradeContent = getUpgradeContent(upgradeParam.getAddon());
        upgradeContent.setContent(upgradeContent.getContent().stream().filter(c -> c.getVersionList().size() > 0).toList());
        upgradeContent.setUpgradeApps(upgradeContent.getContent().stream().map(c -> c.getApp().getAppKey()).toList());
        if (upgradeContent.getContent().size() == 0) throw new CommonException("没有获取到可以升级的内容");

        NiucloudUtils instance = NiucloudUtils.getInstance();

        // 获取下载token
        Map<String, Object> actionQuery = new HashMap<>();
        actionQuery.put("data[product_key]", instance.getProductKey());
        actionQuery.put("data[framework_version]", GlobalConfig.version);
        actionQuery.put("data[app_key]", upgradeContent.getContent().get(0).getApp().getAppKey());
        actionQuery.put("data[version]", upgradeContent.getContent().get(0).getVersion());

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
        vo.setUpgradeContent(upgradeContent);

        if (!upgradeParam.getIsNeedBackup()) {
            vo.getSteps().remove("backupCode");
            vo.getSteps().remove("backupSql");
        }
        if (!upgradeParam.getIsNeedCloudbuild()) {
            vo.getSteps().remove("cloudBuild");
            vo.getSteps().remove("gteCloudBuildLog");
        }

        // 添加升级记录
        JSONArray content = new JSONArray();
        upgradeContent.getContent().stream().forEach(i -> {
            JSONObject item = new JSONObject();
            item.set("app_key", i.getApp().getAppKey());
            item.putByPath("app.name", i.getApp().getAppName());
            item.set("version", i.getVersion());
            item.set("upgrade_version", i.getUpgradeVersion());
            content.put(item);
        });
        SysUpgradeRecordsParam record = new SysUpgradeRecordsParam();
        record.setUpgradeKey(vo.getKey());
        record.setStatus(UpgradeRecordStatusEnum.STATUS_READY.getStatus());
        record.setContent(content.toString());
        sysUpgradeRecordsService.add(record);

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

    @Override
    public void clearUpgradeTask() {
        UpgradeTaskVo vo = this.getUpgradeTask();
        if (vo == null) return;

        SysUpgradeRecordsParam editParam = new SysUpgradeRecordsParam();
        editParam.setStatus(UpgradeRecordStatusEnum.STATUS_CANCEL.getStatus());
        sysUpgradeRecordsService.edit(vo.getKey(), editParam);

        clearUpgradeTask(0);
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
                    vo.setAddon(vo.getUpgradeApps().get(0));
                }
                setUpgradeTaskCache(vo);
            } catch (Exception e) {
                if (e.getMessage().contains("队列")) {
                    throw e;
                } else {
                    vo.setStep(step);
                    vo.getError().add(e.getMessage());
                    setUpgradeTaskCache(vo);
                    this.upgradeErrorHandle(vo);
                    e.printStackTrace();
                    System.out.println("升级异常.");
                }
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
        return WebAppEnvs.get().webRootDownRuntime + "upgrade/" + vo.getKey() + "/";
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

        File downloadDir = new File(upgradeDir(vo) + "/download/" + vo.getAddon());
        Integer chunkSize = 1 * 1024 * 1024;

        if (param.get("index") == null) {
            FileTools.createDirs(downloadDir.getPath());
            FileUtils.cleanDirectory(downloadDir);

            HttpResponse response = new NiucloudUtils.Cloud().build("cloud/upgrade/download").query(query).header("Range", "bytes=0-").method(Method.HEAD).execute();
            if (response.getStatus() != 206) throw new Exception("获取升级文件失败.");

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
                ZipUtil.unzip(file.getPath(), downloadDir.getPath(), Charset.forName(System.getProperty("sun.jnu.encoding")));
                Thread.sleep(1000);
                file.delete();

                int currIndex = vo.getUpgradeApps().indexOf(vo.getAddon());
                List<UpgradeContentVo.Content> upgradeContent = vo.getUpgradeContent().getContent();

                if (vo.getAddon().equals(GlobalConfig.appKey)) {
                    vo.getLog().add("下载更新文件");
                } else {
                    vo.getLog().add("下载"+ upgradeContent.get(currIndex).getApp().getAppName() +"更新文件");
                }
                currIndex++;

                if (currIndex >= vo.getUpgradeApps().size())  return null;

                // 获取下载token
                Map<String, Object> actionQuery = new HashMap<>();
                actionQuery.put("data[product_key]", instance.getProductKey());
                actionQuery.put("data[framework_version]", GlobalConfig.version);
                actionQuery.put("data[app_key]", upgradeContent.get(currIndex).getApp().getAppKey());
                actionQuery.put("data[version]", upgradeContent.get(currIndex).getVersion());

                JSONObject actionToken = niucloudService.getActionToken("upgrade", actionQuery);
                if (actionToken == null || actionToken.getStr("token") == null) throw new CommonException("升级请求失败");

                vo.setAddon((String) actionQuery.get("data[app_key]"));

                Map<String, Object> data = new HashMap<>();
                data.put("token", actionToken.getStr("token"));
                return data;
            }
        }
    }

    /**
     * 备份源码
     *
     * @param vo
     */
    public Map<String, Object> backupCode(UpgradeTaskVo vo) throws Exception {
        try {
            WebAppEnvs envs = WebAppEnvs.get();

            LinkedHashMap<String, String[]> copySrc = new LinkedHashMap<>();
            copySrc.put("admin", new String[]{"node_modules", ".vscode", "dist", ".idea"});
            copySrc.put("niucloud-addon", new String[]{"target"});
            copySrc.put("niucloud-boot", new String[]{"target"});
            copySrc.put("niucloud-core", new String[]{"target"});
            copySrc.put("niucloud-web-app", new String[]{"target"});
            copySrc.put("uni-app", new String[]{"node_modules", "unpackage", "dist"});
            copySrc.put("web", new String[]{"node_modules", ".output", "nuxt", "dist", ".nuxt"});
            copySrc.put("webroot", new String[]{"cloud_build", "upgrade", "weapp_build", "upload", "web-app-boot-exec.jar"});
            copySrc.put("pom.xml", new String[]{});

            File backupDir = new File(upgradeDir(vo) + "/backup/code/");
            String[] keys = copySrc.keySet().toArray(String[]::new);
            int index = 0;

            if (vo.getParams() == null) {
                FileTools.createDirs(backupDir.getPath());
                FileUtils.cleanDirectory(backupDir);
            } else {
                index = (int) vo.getParams().get("index");
            }

            File src = new File(envs.projectRoot, keys[index]);
            if (src.exists()) {
                if (src.isDirectory()) {
                    FileUtils.copyDirectory(src, new File(backupDir, keys[index]), FileTools.createExclusionFilter(copySrc.get(keys[index])));
                    vo.getLog().add(keys[index] + "源码备份成功");
                } else {
                    FileUtils.copyFile(src, new File(backupDir, keys[index]));
                }
            }

            // 添加备份记录
            if (vo.getParams() == null) {
                SysBackupRecords backupRecord = new SysBackupRecords();
                backupRecord.setBackupKey(vo.getKey());
                backupRecord.setVersion(GlobalConfig.version);
                backupRecord.setCreateTime(System.currentTimeMillis() / 1000);
                backupRecord.setContent("自动备份");
                backupRecord.setStatus(UpgradeRecordStatusEnum.STATUS_READY.getStatus());
                sysBackupRecordsMapper.insert(backupRecord);
            }

            index++;
            if (index < keys.length) {
                Map<String, Object> data = new HashMap<>();
                data.put("index", index);
                return data;
            }
            return null;
        } catch (Exception e) {
            SysBackupRecords backupRecord = new SysBackupRecords();
            backupRecord.setStatus(UpgradeRecordStatusEnum.STATUS_FAIL.getStatus());
            sysBackupRecordsMapper.update(backupRecord, new QueryWrapper<SysBackupRecords>().eq("backup_key", vo.getKey()));
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 备份数据库
     * @param vo
     */
    public Map<String, Object> backupSql(UpgradeTaskVo vo) throws Exception {
        // 要排除的表
        String[] excludeTables = {
            "?_sys_schedule_log".replace("?_", GlobalConfig.tablePrefix),
            "?_sys_upgrade_records".replace("?_", GlobalConfig.tablePrefix),
            "?_sys_backup_records".replace("?_", GlobalConfig.tablePrefix),
            "?_sys_area".replace("?_", GlobalConfig.tablePrefix),
            "?_sys_user_log".replace("?_", GlobalConfig.tablePrefix),
            "?_jobs".replace("?_", GlobalConfig.tablePrefix),
            "?_jobs_failed".replace("?_", GlobalConfig.tablePrefix)
        };

        File backupDir = new File(upgradeDir(vo) + "/backup/sql/");

        Map<String, Object> param = vo.getParams();

        if (param == null) {
            FileTools.createDirs(backupDir.getPath());
            FileUtils.cleanDirectory(backupDir);
        }

        try {
            String url = env.getProperty("spring.datasource.url");
            String user = env.getProperty("spring.datasource.username");
            String password = env.getProperty("spring.datasource.password");
            Connection connection = DriverManager.getConnection(url, user, password);
            Long size = Long.valueOf(1024 * 1024 * 2);

            DatabaseBackup db = new DatabaseBackup(connection, backupDir.getPath(), size, Arrays.asList(excludeTables), vo.getKey(), false);
            Object result = db.backupDatabaseSegment(1000);

            if (db.getBackupProgress() == 100) {
                vo.getLog().add("数据库备份完成");
            } else {
                vo.getLog().add("数据库已备份备份" + db.getBackupProgress() + "%");
            }

            if (result instanceof Boolean) {
                SysBackupRecords backupRecord = sysBackupRecordsMapper.selectOne(new QueryWrapper<SysBackupRecords>().eq("backup_key", vo.getKey()));
                backupRecord.setStatus(UpgradeRecordStatusEnum.STATUS_COMPLETE.getStatus());
                backupRecord.setCompleteTime(System.currentTimeMillis() / 1000);
                sysBackupRecordsMapper.updateById(backupRecord);

                vo.getUpgradeContent().setLastBackup(backupRecord);
                return null;
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("index", result);
                return data;
            }
        } catch (Exception e) {
            // 备份记录
            SysBackupRecords backupRecord = new SysBackupRecords();
            backupRecord.setStatus(UpgradeRecordStatusEnum.STATUS_FAIL.getStatus());
            backupRecord.setFailReason(e.getMessage());
            sysBackupRecordsMapper.update(backupRecord, new QueryWrapper<SysBackupRecords>().eq("backup_key", vo.getKey()));

            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 合并更新文件
     * @param vo
     */
    public void coverCode(UpgradeTaskVo vo) throws Exception {
        if (WebAppEnvs.get().envType.equals("dev")) {
            vo.getUpgradeContent().getContent().forEach(item -> {
                String appKey = item.getApp().getAppKey();
                File codeDir = new File(upgradeDir(vo) + "/download/" + appKey);

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
                        if (appKey.equals(GlobalConfig.appKey)) {
                            rootDir = new File(WebAppEnvs.get().projectRoot);
                        } else {
                            rootDir = new File(WebAppEnvs.get().webRootDownAddon, appKey);
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

                        File changeDir = new File(codeDir,  version.getVersionNo());
                        if (changeDir.exists()) {
                            try {
                                FileUtils.copyDirectory(changeDir, rootDir);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    File projectDir = new File(WebAppEnvs.get().projectNiucloudAddon, appKey);
                    if (projectDir.exists()) {
                        try {
                            FileUtils.copyDirectory(new File(WebAppEnvs.get().webRootDownAddon + appKey + "/java"), projectDir);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // 合并resource
                    try {
                        FileUtils.copyDirectory(new File(WebAppEnvs.get().webRootDownAddon + appKey + "/resource"), new File(WebAppEnvs.get().webRootDownResource, appKey));
                    } catch (IOException e) {
                    }
                }
            });

            vo.setStatus("restarting");
            setUpgradeTaskCache(vo);
        } else {
            vo.setStep("coverCode");
            vo.setStatus("restarting");
            setUpgradeTaskCache(vo);
            FileUtils.writeStringToFile(new File(upgradeDir(vo), "upgrade.json"), JSONUtil.parseObj(vo.getUpgradeContent()).toString(), "UTF-8");
            PipeNameUtils.noticeBootRestartByUpgrade(GlobalConfig.applicationName, vo.getKey(), vo.getUpgradeContent().getLastBackup().getBackupKey());
            Thread.sleep(3000);
        }
    }

    /**
     * 处理升级方法
     * @param vo
     */
    public Map<String, Object> handleUpgrade(UpgradeTaskVo vo) {
        int addonIndex = 0;
        int versionIndex = 0;
        if (vo.getParams() != null) {
            addonIndex = (int) vo.getParams().get("addon_index");
            versionIndex = (int) vo.getParams().get("version_index");
        }

        UpgradeContentVo.Content addon = vo.getUpgradeContent().getContent().get(addonIndex);
        UpgradeContentVo.VersionItem version = addon.getVersionList().get(versionIndex);
        String versionNo = version.getVersionNo();

        String addonKey = addon.getApp().getAppKey().equals("niucloud-admin") ? "core" : addon.getApp().getAppKey();

        String sql = AddonModuleContext.readResourceAsStreamToText(addonKey, "upgrade/" + versionNo + ".sql");
        if (!sql.isEmpty()) {
            sql = sql.replace("{{prefix}}", GlobalConfig.tablePrefix);
            try {
                SQLScriptRunnerTools.execScript(sql);
            } catch (Exception e) {
                throw new CommonException("【" + addon.getApp().getAppName() + "】 " + versionNo + "版本升级sql执行出错，错误原因：" + e.getMessage());
            }
        }

        try {
            UpgradeProviderFactory.handle(addonKey, versionNo);
        } catch (Exception e) {
            throw new CommonException("【" + addon.getApp().getAppName() + "】 " + versionNo + "版本升级方法执行出错，错误原因：" + e.getMessage());
        }

        versionIndex++;

        Map<String, Object> data = new HashMap<>();
        if (versionIndex < addon.getVersionList().size()) {
            data.put("addon_index", addonIndex);
            data.put("version_index", versionIndex);
            return data;
        } else {
            addonIndex++;
            if (addonIndex < vo.getUpgradeContent().getContent().size()) {
                data.put("addon_index", addonIndex);
                data.put("version_index", 0);
                return data;
            }
        }
        return null;
    }

    /**
     * 处理vue
     * @param vo
     */
    public void handleVue(UpgradeTaskVo vo) throws Exception {
        WebAppEnvs envs = WebAppEnvs.get();

        for (String key: vo.getUpgradeApps()) {
            if (!key.equals(GlobalConfig.appKey)) {
                File sourceDir = new File(envs.webRootDownAddon + key);
                if (sourceDir.exists()) {
                    addonInstallTools.installVue(key);
                }
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
    public Map<String, Object> refreshMenu(UpgradeTaskVo vo) {
        if (vo.getAddon().equals(GlobalConfig.appKey)) {
            coreMenuService.refreshAddonMenu("core");
        } else {
            coreMenuService.refreshAddonMenu(vo.getAddon());
        }

        int currIndex = vo.getUpgradeApps().indexOf(vo.getAddon()) + 1;
        if (currIndex >= vo.getUpgradeApps().size()) return null;

        vo.setAddon(vo.getUpgradeApps().get(currIndex));

        Map<String, Object> data = new HashMap<>();
        data.put("addon", vo.getUpgradeApps().get(currIndex));
        return data;
    }

    /**
     * 安装计划任务
     * @param vo
     */
    public Map<String, Object> installSchedule(UpgradeTaskVo vo) {
        if (vo.getAddon().equals(GlobalConfig.appKey)) {
            coreScheduleService.installSystemSchedule();
        } else {
            coreScheduleService.installAddonSchedule(vo.getAddon());
        }

        int currIndex = vo.getUpgradeApps().indexOf(vo.getAddon()) + 1;
        if (currIndex >= vo.getUpgradeApps().size()) return null;

        vo.setAddon(vo.getUpgradeApps().get(currIndex));

        Map<String, Object> data = new HashMap<>();
        data.put("addon", vo.getUpgradeApps().get(currIndex));
        return data;
    }

    public void cloudBuild(UpgradeTaskVo vo) {
        cloudBuildService.build("build");
    }

    public Map<String, Object> gteCloudBuildLog(UpgradeTaskVo vo) throws InterruptedException{
        JSONObject log = cloudBuildService.getBuildLog("build");
        if (log != null) {
            JSONArray data = log.getByPath("data.0", JSONArray.class);
            for (int i = 0; i < data.size(); i++) {
                JSONObject item = data.getJSONObject(i);
                if (item.getInt("code", 0).equals(0)) {
                    throw new CommonException(item.getStr("msg"));
                }
                String action = item.getStr("action");
                if (!vo.getLog().contains(action)) {
                    vo.getLog().add(action);
                }
            }
            Thread.sleep(3000);
            return new HashMap<>();
        }
        return null;
    }

    public void upgradeComplete(UpgradeTaskVo vo) {
        vo.setStatus("success");

        for (String key: vo.getUpgradeApps()) {
            if (!key.equals(GlobalConfig.appKey)) {
                Addon addon = JSONUtil.toBean(coreAddonService.getAddonConfig(key), Addon.class);
                coreAddonService.set(addon);
            }
        }

        File backupDir = new File(upgradeDir(vo));
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(vo)), new File(backupDir, DateUtil.now() + ".log"));

        // 变更升级记录
        SysUpgradeRecordsParam editParam = new SysUpgradeRecordsParam();
        editParam.setStatus(UpgradeRecordStatusEnum.STATUS_COMPLETE.getStatus());
        editParam.setCompleteTime(System.currentTimeMillis() / 1000);
        sysUpgradeRecordsService.edit(vo.getKey(), editParam);

        upgradeService.clearUpgradeTask(5);

        cloudBuildService.clearBuildTask();
    }

    /**
     * 升级异常处理
     * @param vo
     */
    public void upgradeErrorHandle(UpgradeTaskVo vo) {
        LinkedHashMap<String, UpgradeTaskVo.Step> steps = new LinkedHashMap<>();
        steps.put("rollback", new UpgradeTaskVo.Step("rollback", "升级失败开始回滚"));

        if (vo.getExecuted().contains("coverCode")) {
            steps.put("restoreCover", new UpgradeTaskVo.Step("restoreCover", "恢复变更文件"));
        }
        if (vo.getExecuted().contains("handleUpgrade")) {
            steps.put("restoreDatabase", new UpgradeTaskVo.Step("restoreDatabase", "恢复数据库"));
        }
        steps.put("restoreComplete", new UpgradeTaskVo.Step("restoreComplete", "回滚完成"));

        vo.setSteps(steps);
        vo.setStep("rollback");
        vo.getLog().add(steps.get("rollback").getTitle());
        vo.getExecuted().add("rollback");

        setUpgradeTaskCache(vo);

        // 变更升级记录
        SysUpgradeRecordsParam editParam = new SysUpgradeRecordsParam();
        editParam.setStatus(UpgradeRecordStatusEnum.STATUS_FAIL.getStatus());
        editParam.setFailReason(JSONUtil.toJsonPrettyStr(vo.getError()));
        sysUpgradeRecordsService.edit(vo.getKey(), editParam);
    }

    @Override
    public void operate(String operate) {
        UpgradeTaskVo vo = this.getUpgradeTask();
        if (vo == null) return;

        switch (operate) {
            case "local":
                vo.setStep("gteCloudBuildLog");
                setUpgradeTaskCache(vo);
                break;
            case "rollback":
                vo.getError().add("失败原因：一键云编译队列任务过多");
                upgradeErrorHandle(vo);
                break;
        }
    }

    /**
     * 恢复文件备份
     */
    public Map<String, Object> restoreCover(UpgradeTaskVo vo) throws Exception {
        SysBackupRecords backup = vo.getUpgradeContent().getLastBackup();

        if (WebAppEnvs.get().envType.equals("dev")) {
            File backupDir = new File(WebAppEnvs.get().webRootDownRuntime + "upgrade/" + backup.getBackupKey() + "/backup/code/");
            if (!backupDir.exists()) return null;

            try {
                int index = 0;
                if (vo.getParams() != null) {
                    index = (int) vo.getParams().get("index");
                }

                File file = backupDir.listFiles()[index];
                if (file.isDirectory()) {
                    FileUtils.copyDirectory(file, new File(WebAppEnvs.get().projectRoot, file.getName()));
                } else {
                    FileUtils.copyFile(file, new File(WebAppEnvs.get().projectRoot, file.getName()));
                }

                index++;
                if (index < backupDir.listFiles().length) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("index", index);
                    return data;
                }
                return null;
            } catch (Exception e) {
                vo.getError().add("变更文件恢复失败，错误信息：" + e.getMessage());
                String dir = "webroot/upgrade/" + backup.getBackupKey() + "/backup/code";
                vo.getError().add("变更文件恢复失败请找到" + dir + "目录下的备份文件手动进行恢复");
                return null;
            }
        } else {
            vo.setStep("restoreCover");
            vo.setStatus("restarting");
            setUpgradeTaskCache(vo);

            PipeNameUtils.noticeBootRestartByUpgradeRollback(GlobalConfig.applicationName, backup.getBackupKey());
            Thread.sleep(3000);

            return null;
        }
    }

    /**
     * 恢复数据库
     */
    public Map<String, Object> restoreDatabase(UpgradeTaskVo vo) {
        SysBackupRecords backup = vo.getUpgradeContent().getLastBackup();
        try {
            File backupDir = new File(WebAppEnvs.get().webRootDownRuntime + "upgrade/" + backup.getBackupKey() + "/backup/sql/");
            File[] files = backupDir.listFiles();
            Arrays.sort(files);

            int index = 0;
            if (vo.getParams() != null) {
                index = (int) vo.getParams().get("index");
            }

            File file = backupDir.listFiles()[index];
            System.out.println("执行数据库脚本：" + file.getName());
            SQLScriptRunnerTools.execScript(file);

            index++;
            if (index < files.length) {
                Map<String, Object> data = new HashMap<>();
                data.put("index", index);
                return data;
            }

            LambdaUpdateWrapper<Site> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Site::getAppType, "admin");
            wrapper.set(Site::getSiteId, 0);
            siteMapper.update(null, wrapper);
            return null;
        } catch (Exception e) {
            vo.getError().add("数据库回滚失败，错误信息：" + e.getMessage());
            String dir = "webroot/upgrade/" + backup.getBackupKey() + "/backup/sql";
            vo.getError().add("数据库回滚失败请找到" + dir + "目录下的数据库备份文件手动进行恢复");
            return null;
        }
    }

    /**
     * 回滚完成
     * @param vo
     */
    public void restoreComplete(UpgradeTaskVo vo) {
        vo.setStatus("rollback");
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
                throw new CommonException(((InvocationTargetException) e).getCause().getMessage());
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
