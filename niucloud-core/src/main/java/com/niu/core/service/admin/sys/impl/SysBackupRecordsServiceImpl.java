package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.DatabaseBackup;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.utils.file.PipeNameUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.sys.SysBackupRecords;
import com.niu.core.entity.sys.SysUpgradeRecords;
import com.niu.core.enums.sys.BackupRecordStatusEnum;
import com.niu.core.enums.sys.UpgradeRecordStatusEnum;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysBackupRecordsMapper;
import com.niu.core.service.admin.sys.ISysBackupRecordsService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.BackupTaskVo;
import com.niu.core.service.admin.sys.vo.SysBackupRecordsListVo;
import com.niu.core.service.admin.sys.vo.SysUpgradeRecordsListVo;
import com.niu.core.service.admin.upgrade.vo.UpgradeTaskVo;
import com.niu.core.service.core.app.tools.SQLScriptRunnerTools;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysBackupRecordsServiceImpl implements ISysBackupRecordsService {
    private final String STATUS_READY = "ready";
    private final String STATUS_FAIL = "fail";

    @Resource
    private SysBackupRecordsMapper sysBackupRecordsMapper;

    @Resource
    Environment env;

    @Lazy
    @Resource
    ISysBackupRecordsService sysUpgradeRecordsService;

    @Resource
    SiteMapper siteMapper;

    @Override
    public PageResult<SysBackupRecordsListVo> page(PageParam pageParam, SysBackupRecordsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysBackupRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        if (searchParam != null) {
            if (searchParam.getContent() != null) {
                queryWrapper.like("content", searchParam.getContent());
            }
        }

        IPage<SysBackupRecords> iPage = sysBackupRecordsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysBackupRecordsListVo> list = new ArrayList<>();
        for (SysBackupRecords item : iPage.getRecords()) {
            SysBackupRecordsListVo vo = new SysBackupRecordsListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setCreateTime(DateUtils.timestampToString(item.getCreateTime()));
            vo.setCompleteTime(DateUtils.timestampToString(item.getCompleteTime()));
            vo.setStatusName(BackupRecordStatusEnum.getNameByStatus(vo.getStatus()));
            vo.setBackupDir("webroot/runtime/upgrade/"+vo.getBackupKey()+"/backup");
            list.add(vo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public void add(SysBackupRecordsParam addParam) {
        SysBackupRecords model = new SysBackupRecords();
        BeanUtils.copyProperties(addParam, model);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        sysBackupRecordsMapper.insert(model);
    }

    @Override
    public void edit(Integer id, SysBackupRecordsParam editParam) {
        SysBackupRecords model = sysBackupRecordsMapper.selectOne(
            new QueryWrapper<SysBackupRecords>()
                .eq("id", id));
        Assert.notNull(model, "备份记录不存在");

        BeanUtils.copyProperties(editParam, model);
        sysBackupRecordsMapper.updateById(model);
    }

    @Override
    public void clear(long threshold) {
        sysBackupRecordsMapper.delete(new QueryWrapper<SysBackupRecords>()
                .lt("create_time", threshold).in("status", STATUS_READY, STATUS_FAIL));
    }

    @Override
    public void del(SysBackupRecordsDelParam delParam) {
        QueryWrapper queryWrapper = new QueryWrapper<SysUpgradeRecords>();

        if (delParam.getIds() instanceof ArrayList<?>) {
            List<Integer> intArray = new ArrayList<>();
            ((ArrayList<?>) delParam.getIds()).forEach(item -> {
                Assert.notNull(item, "id不能为空");
                intArray.add((Integer) item);
            });
            queryWrapper.in("id", intArray);
        } else {
            queryWrapper.eq("id", delParam.getIds());
        }

        List<SysBackupRecords> backupRecords = sysBackupRecordsMapper.selectList(queryWrapper);
        if (ObjectUtil.isNotEmpty(backupRecords)) {
            for (SysBackupRecords item : backupRecords) {
                File file = new File(backupDir(item.getBackupKey()), "backup");
                if (file.exists()) {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (Exception e) {
                    }
                }
            }
            sysBackupRecordsMapper.delete(queryWrapper);
        }

    }

    /**
     * 恢复备份
     * @param restoreParam
     * @return
     */
    @Override
    public BackupTaskVo restore(BackupRestoreParam restoreParam) {
        SysBackupRecords record = checkDir(restoreParam);

        BackupTaskVo vo = getRestoreTask();
        if (vo == null) {
            vo = new BackupTaskVo();
            LinkedHashMap<String, BackupTaskVo.Step> steps = new LinkedHashMap<>();
            steps.put("startRestore", new BackupTaskVo.Step("startRestore", "开始恢复"));
            steps.put("backupCode", new BackupTaskVo.Step("backupCode", "备份源码"));
            steps.put("backupSql", new BackupTaskVo.Step("backupSql", "备份数据库"));
            steps.put("restoreBackupComplete", new BackupTaskVo.Step("restoreBackupComplete", "备份完成"));
            steps.put("restoreCode", new BackupTaskVo.Step("restoreCode", "恢复源码备份"));
            steps.put("restoreSql", new BackupTaskVo.Step("restoreSql", "恢复数据库备份"));
            steps.put("restoreComplete", new BackupTaskVo.Step("restoreComplete", "恢复完成"));
            vo.setKey(RandomUtil.randomString(10));
            vo.setSteps(steps);
            vo.setStep("startRestore");
            vo.getExecuted().add("startRestore");
            vo.setContent("开始恢复");
            vo.setTask("");

            SysBackupRecordsParam addParam = new SysBackupRecordsParam();
            addParam.setBackupKey(vo.getKey());
            addParam.setStatus(BackupRecordStatusEnum.STATUS_READY.getStatus());
            addParam.setContent("自动备份");
            addParam.setVersion(GlobalConfig.version);
            add(addParam);
        }
        vo.setBackupRecord(record);
        if (!vo.getTask().equals("")) return vo;

        List<String> steps = vo.getSteps().keySet().stream().collect(Collectors.toList());
        String step = steps.indexOf(vo.getStep()) < steps.size() - 1 ? steps.get(steps.indexOf(vo.getStep()) + 1) : "";

        if (!step.isEmpty()) {
            if (!vo.getExecuted().contains(step)) {
                vo.getExecuted().add(step);
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
                setBackupRestoreTaskCache(vo);
            } catch (Exception e) {
                vo.setStep(step);
                vo.setTask("fail");
                vo.setContent("备份恢复失败，稍后请手动恢复，备份文件路径：webroot/runtime/upgrade/"+ record.getBackupKey() +"/backup，失败原因：" + e.getMessage());
                setBackupRestoreTaskCache(vo);
                // 删除备份记录
                sysBackupRecordsMapper.delete(new QueryWrapper<SysBackupRecords>().eq("backup_key", vo.getKey()));
                e.printStackTrace();
                System.out.println("恢复异常.");

                sysUpgradeRecordsService.clearRestoreTaskCache(5);
            }
        }
        return vo;
    }

    /**
     *
     * 获取升级文件目录
     * @param key
     * @return
     */
    private String backupDir(String key) {
        return WebAppEnvs.get().webRootDownRuntime + "upgrade/" + key + "/";
    }

    @Override
    public BackupTaskVo backup() {
        BackupTaskVo vo = getBackupTask();
        if (vo == null) {
            vo = new BackupTaskVo();
            vo.setKey(RandomUtil.randomString(10));
            vo.setStep("startBackup");
            vo.getExecuted().add("startBackup");
            vo.setContent("开始备份");

            SysBackupRecordsParam addParam = new SysBackupRecordsParam();
            addParam.setBackupKey(vo.getKey());
            addParam.setStatus(BackupRecordStatusEnum.STATUS_READY.getStatus());
            addParam.setContent("手动备份");
            addParam.setVersion(GlobalConfig.version);
            add(addParam);
        }

        List<String> steps = vo.getSteps().keySet().stream().collect(Collectors.toList());
        String step = steps.indexOf(vo.getStep()) < steps.size() - 1 ? steps.get(steps.indexOf(vo.getStep()) + 1) : "";

        if (!step.isEmpty()) {
            if (!vo.getExecuted().contains(step)) {
                vo.getExecuted().add(step);
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
                setBackupTaskCache(vo);
            } catch (Exception e) {
                vo.setStep(step);
                vo.setTask("fail");
                vo.setContent("备份失败，稍后请重新手动备份，失败原因：" + e.getMessage());
                setBackupTaskCache(vo);
                // 删除备份记录
                sysBackupRecordsMapper.delete(new QueryWrapper<SysBackupRecords>().eq("backup_key", vo.getKey()));
                e.printStackTrace();
                System.out.println("备份异常.");

                sysUpgradeRecordsService.clearBackupTaskCache(5);
            }
        }
        return vo;
    }

    public Map<String, Object> backupCode(BackupTaskVo vo) throws Exception {
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

            File backupDir = new File(backupDir(vo.getKey()) + "/backup/code/");
            String[] keys = copySrc.keySet().stream().filter(key -> new File(envs.projectRoot, key).exists()).toArray(String[]::new);
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
                    vo.setContent(keys[index] + "源码备份成功");
                } else {
                    FileUtils.copyFile(src, new File(backupDir, keys[index]));
                }
            }

            index++;
            if (index < keys.length) {
                Map<String, Object> data = new HashMap<>();
                data.put("index", index);
                return data;
            }
            return null;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 备份数据库
     * @param vo
     */
    public Map<String, Object> backupSql(BackupTaskVo vo) throws Exception {
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

        File backupDir = new File(backupDir(vo.getKey()) + "/backup/sql/");

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
                vo.setContent("数据库备份成功");
            } else {
                vo.setContent("数据库已备份备份" + db.getBackupProgress() + "%");
            }

            if (result instanceof Boolean) {
                return null;
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("index", result);
                return data;
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 备份完成
     * @param vo
     */
    public void backupComplete(BackupTaskVo vo) {
        SysBackupRecords backupRecord = new SysBackupRecords();
        backupRecord.setStatus(UpgradeRecordStatusEnum.STATUS_COMPLETE.getStatus());
        backupRecord.setCompleteTime(System.currentTimeMillis() / 1000);
        sysBackupRecordsMapper.update(backupRecord, new QueryWrapper<SysBackupRecords>().eq("backup_key", vo.getKey()));

        vo.setTask("end");

        sysUpgradeRecordsService.clearBackupTaskCache(5);
    }

    public Map<String, Object> restoreCode(BackupTaskVo vo) throws InterruptedException {
        SysBackupRecords backup = vo.getBackupRecord();

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
                throw new CommonException("变更文件恢复失败，错误信息：" + e.getMessage());
            }
        } else {
            vo.setStep("restoreCode");
            vo.setTask("restarting");
            setBackupRestoreTaskCache(vo);
            PipeNameUtils.noticeBootRestartByUpgradeRollback(GlobalConfig.applicationName, backup.getBackupKey());
            Thread.sleep(3000);
            return null;
        }
    }

    public Map<String, Object> restoreSql(BackupTaskVo vo) {
        SysBackupRecords backup = vo.getBackupRecord();
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
            throw new CommonException("数据库回滚失败" + e.getMessage());
        }
    }

    /**
     * 备份完成
     * @param vo
     */
    public void restoreBackupComplete(BackupTaskVo vo) {
        SysBackupRecords backupRecord = new SysBackupRecords();
        backupRecord.setStatus(UpgradeRecordStatusEnum.STATUS_COMPLETE.getStatus());
        backupRecord.setCompleteTime(System.currentTimeMillis() / 1000);
        sysBackupRecordsMapper.update(backupRecord, new QueryWrapper<SysBackupRecords>().eq("backup_key", vo.getKey()));
    }

    /**
     * 恢复完成
     * @param vo
     */
    public void restoreComplete(BackupTaskVo vo) {
        vo.setTask("end");
        sysUpgradeRecordsService.clearRestoreTaskCache(5);
    }

    public void setBackupTaskCache(BackupTaskVo vo) {
        Cached cache = CacheFactory.getCacheOperator();
        cache.put("backup_task", JSONUtil.parseObj(vo).toString(), 1800);
    }

    public void setBackupRestoreTaskCache(BackupTaskVo vo) {
        Cached cache = CacheFactory.getCacheOperator();
        cache.put("backup_restore_task", JSONUtil.parseObj(vo).toString(), 1800);
    }

    @Async
    @Override
    public void clearBackupTaskCache(int delayed) {
        if (delayed > 0) {
            try {
                Thread.sleep(delayed * 1000);
            } catch (Exception e) {
            }
        }
        Cached cache = CacheFactory.getCacheOperator();
        cache.remove("backup_task");
    }

    @Async
    @Override
    public void clearRestoreTaskCache(int delayed) {
        if (delayed > 0) {
            try {
                Thread.sleep(delayed * 1000);
            } catch (Exception e) {
            }
        }
        Cached cache = CacheFactory.getCacheOperator();
        cache.remove("backup_restore_task");
    }

    @Override
    public BackupTaskVo getBackupTask() {
        Cached cache = CacheFactory.getCacheOperator();
        Object data = cache.get("backup_task");
        if (data == null) return null;
        return JSONUtil.toBean(JSONUtil.parseObj(data), BackupTaskVo.class);
    }

    @Override
    public BackupTaskVo getRestoreTask() {
        Cached cache = CacheFactory.getCacheOperator();
        Object data = cache.get("backup_restore_task");
        if (data == null) return null;
        return JSONUtil.toBean(JSONUtil.parseObj(data), BackupTaskVo.class);
    }

    /**
     * 检测备份文件是否存在
     * @param param
     */
    @Override
    public SysBackupRecords checkDir(BackupRestoreParam param) {
        SysBackupRecords record = sysBackupRecordsMapper.selectById(param.getId());
        Assert.notNull(record, "备份记录不存在");
        if (!record.getStatus().equals(BackupRecordStatusEnum.STATUS_COMPLETE.getStatus())) throw new RuntimeException("备份记录未完成");

        File code = new File(WebAppEnvs.get().webRootDownRuntime + "upgrade/"+ record.getBackupKey() + "/backup/code");
        if (!code.exists()) throw new RuntimeException("未找到备份的源码文件");

        File sql = new File(WebAppEnvs.get().webRootDownRuntime + "upgrade/"+ record.getBackupKey() + "/backup/sql");
        if (!sql.exists()) throw new RuntimeException("未找到备份的数据库文件");

        return record;
    }

    /**
     * 检测目录权限
     * @return
     */
    @Override
    public JSONObject checkPermission() {
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
