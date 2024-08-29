package com.niu.core.service.admin.niucloud.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.component.context.WebAppEnvs;
import com.niu.core.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.NiucloudUtils;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.service.admin.niucloud.ICloudBuildService;
import com.niu.core.service.admin.niucloud.INiucloudService;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * 获取官方框架服务层
 */
@Service
public class CloudBuildServiceImpl implements ICloudBuildService {

    @Resource
    Cached cached;

    @Resource
    INiucloudService niucloudService;

    @Resource
    ICoreAddonService coreAddonService;

    private JSONObject buildTask;


    /**
     * 获取编译任务
     * @return
     */
    public JSONObject getBuildTask(String mode) {
        if (cached.get("cloud_build_task") == null) return null;
        this.buildTask = (JSONObject) cached.get("cloud_build_task");
        if (!this.buildTask.getStr("mode").equals(mode)) return null;
        return this.buildTask;
    }

    /**
     * 编译前环境检测
     *
     * @return
     */
    public JSONObject buildPreCheck() {
        JSONObject checkResult = new JSONObject();
        checkResult.put("is_pass", true);
        checkResult.put("dir", new HashMap());
        return checkResult;
    }

    /**
     * 编译
     *
     * @return
     */
    public JSONObject build(String mode) {
        if (this.buildTask != null) throw new CommonException("已有正在执行中的编译任务");

        String taskKey = RandomUtil.randomString(10);

        this.buildTask = new JSONObject();
        this.buildTask.set("mode", mode);
        this.buildTask.set("task_key", taskKey);

        String tempDir = WebAppEnvs.get().webRootDownRuntime + "cloud_build/" + taskKey + "/";
        String packageDir = tempDir + "package/";
        FileTools.createDirs(packageDir);

        buildPackage(packageDir);

        File zipFile = ZipUtil.zip(packageDir, tempDir + "build.zip");

        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> actionQuery = new HashMap<>();
        actionQuery.put("data[product_key]", instance.getProductKey());
        JSONObject actionToken = niucloudService.getActionToken("cloudbuild", actionQuery);

        Map<String, Object> query = new HashMap<>();
        query.put("authorize_code", instance.getCode());
        query.put("timestamp", System.currentTimeMillis() / 1000);
        query.put("token", actionToken == null ? "" : actionToken.getStr("token"));

        HttpResponse response = new NiucloudUtils.Cloud().build("cloud/build").query(query)
                .func(i -> {
                    i.form("file", zipFile, "build.zip");
                })
                .method(Method.POST).execute();

        JSONObject res =  JSONUtil.parseObj(response.body());

        if (!res.getInt("code", 0).equals(1)) throw new CommonException(res.getStr("msg"));

        this.buildTask.set("timestamp", query.get("timestamp"));
        cached.put("cloud_build_task", this.buildTask);

        return this.buildTask;
    }

    /**
     * 获取编译执行日志
     *
     * @return
     */
    public JSONObject getBuildLog(String mode) {
        if (this.buildTask == null) return null;
        if (!this.buildTask.getStr("mode").equals(mode)) return null;

        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("authorize_code", instance.getCode());
        query.put("timestamp", this.buildTask.getStr("timestamp"));

        HttpResponse response = new NiucloudUtils.Cloud().build("cloud/get_build_logs").query(query).method(Method.GET).execute();
        if (!JSONUtil.isJson(response.body())) return null;

        JSONObject res =  JSONUtil.parseObj(response.body());

        JSONArray data = res.getByPath("data.0", JSONArray.class);
        if (data.size() > 0) {
            JSONObject last = data.getJSONObject(data.size() - 1);
            if (last.getInt("percent", 0).equals(100) && last.getInt("code", 0).equals(1)) {
                res = buildSuccess(res);
            }
        }
        return res;
    }

    /**
     * 编译完成
     *
     * @param buildLog
     * @return
     */
    private JSONObject buildSuccess(JSONObject buildLog) {
        JSONArray logArray = buildLog.getByPath("data.0", JSONArray.class);
        try {
            NiucloudUtils instance = NiucloudUtils.getInstance();

            Map<String, Object> query = new HashMap<>();
            query.put("authorize_code", instance.getCode());
            query.put("timestamp", this.buildTask.getStr("timestamp"));

            String tempDir = WebAppEnvs.get().webRootDownRuntime + "cloud_build/" + this.buildTask.getStr("task_key") + "/";
            Integer chunkSize = 1 * 1024 * 1024;

            if (this.buildTask.getInt("index") == null) {
                HttpResponse response = new NiucloudUtils.Cloud().build("cloud/build_download").query(query).header("Range", "bytes=0-").method(Method.HEAD).execute();
                String totalLength = response.header("Content-range");
                String length = ObjectUtil.defaultIfNull(totalLength.split("/")[1], "");
                Double step = Math.ceil(Double.valueOf(length) / chunkSize);
                this.buildTask.set("index", 0);
                this.buildTask.set("step", step.intValue());
                this.buildTask.set("length", length);
            } else {
                File file = new File(tempDir + "download.zip");
                FileOutputStream fos = new FileOutputStream(file, true);

                Integer index = this.buildTask.getInt("index");
                Integer step = this.buildTask.getInt("step");
                if (index + 1 <= step) {
                    Integer start = index * chunkSize;
                    Integer end = (index + 1) * chunkSize;
                    if (end > this.buildTask.getInt("length")) end = this.buildTask.getInt("length");

                    HttpResponse response = new NiucloudUtils.Cloud().build("cloud/build_download").query(query).header("Range", "bytes=" + start + "-" + end).method(Method.GET).execute();
                    fos.write(response.bodyBytes());

                    this.buildTask.set("index", index + 1);

                    JSONObject log = new JSONObject();
                    log.set("code", 1);
                    log.set("action", "编译包下载中,已下载" + Math.round((double) index / step * 100) + "%");
                    log.set("percent", 100);
                    logArray.put(log);
                } else {
                    JSONObject log = new JSONObject();
                    log.set("code", 1);
                    log.set("action", "build_success");
                    log.set("percent", 100);
                    logArray.put(log);

                    FileTools.createDirs(tempDir + "download");
                    File unzipFile = ZipUtil.unzip(file.getPath(), tempDir + "download");
                    FileUtils.copyDirectory(unzipFile, new File(WebAppEnvs.get().webRoot));
                    this.clearBuildTask();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject log = new JSONObject();
            log.set("code", 0);
            log.set("action", "");
            log.set("msg", e.getMessage());
            log.set("percent", 100);
            logArray.put(log);
        }
        buildLog.putByPath("data.0", logArray);
        return buildLog;
    }

    /**
     * 打包编译文件
     * @param packageDir
     */
    private void buildPackage(String packageDir) {
        String rootDir = "";
        // 开发模式
        if (WebAppEnvs.get().envType.equals("dev")) {
            rootDir = WebAppEnvs.get().projectRoot + "/";
        } else {
            rootDir = WebAppEnvs.get().webRootDownRuntime;
        }

        try {
            File uniApp = new File(rootDir + "uni-app/");
            if (uniApp.exists()) {
                String[] exclusionDir = {"node_modules", "unpackage", "dist"};
                FileTools.createDirs(packageDir + "uni-app");
                FileUtils.copyDirectory(uniApp, new File(packageDir + "uni-app/"), createExclusionFilter(exclusionDir));
            }
            File admin = new File(rootDir + "admin/");
            if (admin.exists()) {
                String[] exclusionDir = {"node_modules", ".vscode", "dist", ".idea"};
                FileTools.createDirs(packageDir + "admin");
                FileUtils.copyDirectory(admin, new File(packageDir + "admin/"), createExclusionFilter(exclusionDir));
            }
            File web = new File(rootDir + "web/");
            if (web.exists()) {
                String[] exclusionDir = {"node_modules", ".output", "nuxt"};
                FileTools.createDirs(packageDir + "web");
                FileUtils.copyDirectory(web, new File(packageDir + "web/"), createExclusionFilter(exclusionDir));
            }

            // 打包自定义端口
            Map<String, InstallAddonListVo> addonList = coreAddonService.getInstallAddonList();
            for (String addon: addonList.keySet()) {
                JSONObject addonConfig = coreAddonService.getAddonConfig(addon);
                JSONArray ports = ObjectUtil.defaultIfNull(addonConfig.getJSONArray("port"), new JSONArray());
                for (int i = 0; i < ports.size(); i++) {
                    JSONObject port = ports.getJSONObject(i);
                    File portFile = new File(WebAppEnvs.get().webRootDownAddon + addon);
                    if (portFile.isDirectory() && portFile.exists()) {
                        FileTools.createDirs(packageDir + port);
                        FileUtils.copyDirectory(portFile, new File(packageDir + port));
                        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(port), new File(packageDir + port, "info.json"));
                    }
                }
            }
        } catch (Exception e) {
            clearBuildTask();
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 排除目录
     * @param dirs
     * @return
     */
    private static IOFileFilter createExclusionFilter(String[] dirs) {
        // 定义要排除的目录
        List<String> excludedDirs = Arrays.asList(dirs);

        // 创建一个过滤器来排除特定子目录
        IOFileFilter excludeSubDirsFilter = FileFilterUtils.trueFileFilter(); // 初始化为 false，表示过滤所有文件或目录

        // 循环创建过滤器
        for (String dirName : excludedDirs) {
            IOFileFilter tempFilter = FileFilterUtils.nameFileFilter(dirName);
            excludeSubDirsFilter = FileFilterUtils.and(excludeSubDirsFilter, FileFilterUtils.notFileFilter(tempFilter));
        }

        return excludeSubDirsFilter;
    }

    /**
     * 清除编译任务
     */
    public void clearBuildTask() {
        if (this.buildTask == null) return;
        File tempDir = new File(WebAppEnvs.get().webRootDownRuntime + "cloud_build/" + this.buildTask.getStr("task_key"));
        try {
            if (tempDir.exists()) FileUtils.deleteDirectory(tempDir);
        } catch (Exception e) {
        }
        cached.remove("cloud_build_task");
        this.buildTask = null;
    }
}
