package com.niu.core.service.core.channel.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.NiucloudUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.entity.addon.Addon;
import com.niu.core.service.admin.channel.param.AppVersionAddParam;
import com.niu.core.service.admin.niucloud.INiucloudService;
import com.niu.core.service.admin.sys.vo.SysMapVo;
import com.niu.core.service.core.addon.AddonInstallTools;
import com.niu.core.service.core.channel.ICoreAppCloudService;
import com.niu.core.service.core.channel.ICoreAppService;
import com.niu.core.service.core.channel.param.GenerateSignCertParam;
import com.niu.core.service.core.channel.vo.AppCompileLogVo;
import com.niu.core.service.core.channel.vo.AppConfigVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.weapp.param.WeappUploadParam;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CoreAppCloudServiceImpl implements ICoreAppCloudService {

    @Resource
    AddonInstallTools addonInstallTools;

    @Resource
    INiucloudService niucloudService;

    @Resource
    ICoreAppService coreAppService;

    @Resource
    ICoreSiteService coreSiteService;

    @Resource
    ICoreSysConfigService coreSysConfigService;

    @Resource
    Cached cached;

    @Override
    public String appCloudBuid(AppVersionAddParam param) {
        AppConfigVo appConfig = coreAppService.getConfig(param.getSiteId());

        if (ObjectUtil.isEmpty(appConfig.getAppName())) throw new CommonException("请先配置应用名称");
        if (ObjectUtil.isEmpty(appConfig.getUniAppId())) throw new CommonException("请先配置应用ID");
        if (ObjectUtil.isEmpty(appConfig.getAndroidAppKey())) throw new CommonException("请先配置应用密钥");
        if (ObjectUtil.isEmpty(appConfig.getApplicationId())) throw new CommonException("请先配置应用包名");

        String taskKey = String.valueOf(System.currentTimeMillis() / 1000);

        String tempDir = WebAppEnvs.get().webRootDownRuntime + "app_build/" + taskKey + "/";
        String packageDir = tempDir + "package/";
        FileTools.createDirs(packageDir);

        handleUniapp(packageDir + "uni-app/", param, appConfig);

        String wapUrl = coreSysConfigService.getSceneDomain(param.getSiteId()).getWapUrl();

        SysMapVo mapConfig = coreSysConfigService.getMap(param.getSiteId());

        JSONObject build = new JSONObject();
        build.set("app_name", appConfig.getAppName());
        build.set("uni_app_id", appConfig.getUniAppId());
        build.set("wechat_app_id", ObjectUtil.defaultIfNull(appConfig.getWechatAppId(), ""));
        build.set("wechat_app_secret", ObjectUtil.defaultIfNull(appConfig.getWechatAppSecret(), ""));
        build.set("android_app_key", appConfig.getAndroidAppKey());
        build.set("application_id", appConfig.getApplicationId());
        build.set("privacy_agreement", wapUrl + "/app/pages/auth/agreement?key=privacy&=");
        build.set("service_agreement", wapUrl + "/app/pages/auth/agreement?key=service&=");
        build.set("qq_map_key", mapConfig.getKey());
        build.set("amap_key", "");
        build.set("version_name", param.getVersionName());
        build.set("version_code", param.getVersionCode());
        build.putByPath("cert.type", param.getCert().getType());
        build.putByPath("cert.file", param.getCert().getFile());
        build.putByPath("cert.key_alias", param.getCert().getKeyAlias());
        build.putByPath("cert.key_password", param.getCert().getKeyPassword());
        build.putByPath("cert.store_password", param.getCert().getStorePassword());

        try {
            // 写入build.json文件
            FileUtils.writeStringToFile(new File(packageDir, "build.json"), build.toString(), "UTF-8");

            // 拷贝证书文件
            if (param.getCert().getType().equals("private")){
                File certFile = new File(WebAppEnvs.get().webRootDownResource, param.getCert().getFile());
                if (!certFile.exists()) throw new CommonException("证书文件不存在");

                FileUtils.copyFile(certFile, new File(packageDir, "cert.jks"));
            }

            // 拷贝icon文件
            File iconFile = new File(WebAppEnvs.get().webRootDownResource, param.getBuild().getIcon());
            if (!iconFile.exists()) throw new CommonException("icon文件不存在");
            FileUtils.copyFile(iconFile, new File(packageDir, "drawable.zip"));

            // 将临时目录下文件生成压缩包
            File zipFile = ZipUtil.zip(packageDir, tempDir + "app.zip");

            NiucloudUtils instance = NiucloudUtils.getInstance();

            Map<String, Object> actionQuery = new HashMap<>();
            actionQuery.put("data[product_key]", instance.getProductKey());
            JSONObject actionToken = niucloudService.getActionToken("appbuild", actionQuery);

            Map<String, Object> query = new HashMap<>();
            query.put("authorize_code", instance.getCode());
            query.put("timestamp", taskKey);
            query.put("token", actionToken == null ? "" : actionToken.getStr("token"));

            HttpResponse response = new NiucloudUtils.Cloud().build("cloud/appbuild").query(query)
                .func(i -> {
                    i.form("file", zipFile, "app.zip");
                })
                .method(Method.POST).execute();

            JSONObject res =  JSONUtil.parseObj(response.body());

            if (!res.getInt("code", 0).equals(1)) throw new CommonException(res.getStr("msg"));

            return taskKey;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 整理编译文件
     *
     * @param packageDir
     * @return
     */
    private void handleUniapp(String packageDir, AppVersionAddParam param, AppConfigVo appConfig) {
        String rootDir = "";
        // 开发模式
        if (WebAppEnvs.get().envType.equals("dev")) {
            rootDir = WebAppEnvs.get().projectRoot + "/";
        } else {
            rootDir = WebAppEnvs.get().webRootDownRuntime;
        }

        try {
            File uniApp = new File(rootDir + "uni-app/");
            if (!uniApp.exists()) throw new CommonException("未找到uni-app源码");

            String[] exclusionDir = {"node_modules", "unpackage", "dist"};
            FileUtils.copyDirectory(uniApp, new File(packageDir), FileTools.createExclusionFilter(exclusionDir));

            File envFile = new File(packageDir + ".env.production");
            if (!envFile.exists()) throw new CommonException("uni-app .env.production 文件缺失");

            String wapUrl = RequestUtils.getDomain(true);

            String content = FileUtils.readFileToString(envFile, "UTF-8");
            content = content.replace("VITE_APP_BASE_URL = ''", "VITE_APP_BASE_URL = '" + wapUrl + "/api/" + "'");
            content = content.replace("VITE_IMG_DOMAIN = ''", "VITE_IMG_DOMAIN = '" + wapUrl + "'");
            content = content.replace("VITE_SITE_ID = ''", "VITE_SITE_ID = '" + param.getSiteId().toString() + "'");
            FileUtil.writeUtf8String(content, envFile);

            List<String> addons = coreSiteService.getSiteAddons(param.getSiteId()).stream().map(Addon::getKey).collect(Collectors.toList());
            addonInstallTools.handlePagesJson(packageDir, addons);
            addonInstallTools.handleUniappComponent(packageDir, addons);

            JSONObject manifest = new JSONObject();
            manifest.set("name", appConfig.getAppName());
            manifest.set("appid", appConfig.getUniAppId());
            manifest.set("versionName", param.getVersionName());
            manifest.set("versionCode", param.getVersionCode());

            addonInstallTools.mergeUniappManifest(packageDir, manifest);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public AppCompileLogVo getAppCompileLog(String key) {
        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("authorize_code", instance.getCode());
        query.put("timestamp", key);

        HttpResponse response = new NiucloudUtils.Cloud().build("cloud/get_appbuild_logs").query(query).method(Method.GET).execute();
        try {
            AppCompileLogVo vo = new AppCompileLogVo();
            JSONObject log =  JSONUtil.parseObj(response.body());
            vo.setBuildLog(log);

            if (log != null) {
                JSONArray data = ObjectUtil.defaultIfNull(log.getByPath("data.0", JSONArray.class), new JSONArray());
                if (data.size() > 0) {
                    JSONObject last = data.getJSONObject(data.size() - 1);
                    if (last.getInt("code", -1).equals(0)) {
                        vo.setStatus("fail");
                        vo.setFailReason(last.getStr("msg", ""));
                        return vo;
                    }
                    if (last.getInt("percent", 0).equals(100)) {
                        buildSuccess(key, vo);
                    }
                }
            }

            return vo;
        } catch (Exception e) {
            return null;
        }
    }

    private void buildSuccess(String key, AppCompileLogVo vo) {
        JSONArray logArray = vo.getBuildLog().getByPath("data.0", JSONArray.class);
        try {
            NiucloudUtils instance = NiucloudUtils.getInstance();

            Map<String, Object> query = new HashMap<>();
            query.put("authorize_code", instance.getCode());
            query.put("timestamp", key);

            String tempDir = WebAppEnvs.get().webRootDownResource + "upload/download/app/" + key + "/";
            Integer chunkSize = 1 * 1024 * 1024;

            JSONObject buildTask = ObjectUtil.defaultIfNull(JSONUtil.parseObj(cached.get("app_cloud_build" + key)), new JSONObject());

            if (buildTask.getInt("index") == null) {
                FileTools.createDirs(tempDir);

                HttpResponse response = new NiucloudUtils.Cloud().build("cloud/apk_download").query(query).header("Range", "bytes=0-").method(Method.HEAD).execute();
                String totalLength = response.header("Content-range");
                String length = ObjectUtil.defaultIfNull(totalLength.split("/")[1], "");
                Double step = Math.ceil(Double.valueOf(length) / chunkSize);
                String fileName = response.header("filename");

                buildTask.set("index", 0);
                buildTask.set("step", step.intValue());
                buildTask.set("length", length);
                buildTask.set("file_name", fileName);

                cached.put("app_cloud_build" + key, buildTask);
            } else {
                Integer index = buildTask.getInt("index");
                Integer step = buildTask.getInt("step");
                String filename = buildTask.getStr("file_name");

                File file = new File(tempDir + filename);

                if (index + 1 <= step) {
                    Integer start = index * chunkSize;
                    Integer end = (index + 1) * chunkSize;
                    if (end > buildTask.getInt("length")) end = buildTask.getInt("length");

                    HttpResponse response = new NiucloudUtils.Cloud().build("cloud/apk_download").query(query).header("Range", "bytes=" + start + "-" + end).method(Method.GET).execute();
                    try (FileOutputStream fos = new FileOutputStream(file, true)) {
                        fos.write(response.bodyBytes());
                    }

                    buildTask.set("index", index + 1);
                    cached.put("app_cloud_build" + key, buildTask);

                    JSONObject log = new JSONObject();
                    log.set("code", 1);
                    log.set("action", "安装包下载中,已下载" + Math.round((double) index / step * 100) + "%");
                    log.set("percent", 100);
                    logArray.put(log);
                } else {
                    JSONObject log = new JSONObject();
                    log.set("code", 1);
                    log.set("action", "build_success");
                    log.set("percent", 100);
                    logArray.put(log);

                    cached.remove("app_cloud_build" + key);

                    vo.setStatus("success");
                    vo.setFilePath("upload/download/app/" + key + "/" + filename);
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

            cached.remove("app_cloud_build" + key);

            vo.setStatus("fail");
            vo.setFailReason(e.getMessage());
        }
        vo.getBuildLog().putByPath("data.0", logArray);
    }

    @Override
    public String generateSignCert(GenerateSignCertParam param) {
        Map<String, Object> query = new HashMap<>();
        query.put("key_alias", param.getKeyAlias());
        query.put("key_password", param.getKeyPassword());
        query.put("store_password", param.getStorePassword());
        query.put("limit", param.getLimit());
        query.put("dname", "CN=" + param.getCn() + ", OU=" + param.getOu() + ", O=" + param.getO() + ", L=" + param.getL() + ", ST=" + param.getSt() + ", C=" + param.getC());

        HttpResponse response = new NiucloudUtils.Cloud().build("cloud/getcert").query(query).method(Method.GET).execute();

        if (response.header("Content-Type").equals("application/json")) {
            JSONObject content = JSONUtil.parseObj(response.body());
            if (content.getInt("code", -1) == 0) {
                throw new CommonException(ObjectUtil.defaultIfBlank(content.getStr("msg", ""), "获取证书失败"));
            }
        }

        String tempDir = WebAppEnvs.get().webRootDownResource + "upload/download/cert/";
        FileTools.createDirs(tempDir);

        try {
            File file = new File(tempDir + param.getKeyAlias() + ".zip");
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(response.bodyBytes());
            return "upload/download/cert/" + param.getKeyAlias() + ".zip";
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(e.getMessage());
        }
    }
}
