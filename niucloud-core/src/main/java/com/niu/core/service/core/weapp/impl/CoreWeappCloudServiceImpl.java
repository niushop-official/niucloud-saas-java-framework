package com.niu.core.service.core.weapp.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.NiucloudUtils;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.entity.addon.Addon;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.service.admin.niucloud.INiucloudService;
import com.niu.core.service.core.addon.AddonInstallTools;
import com.niu.core.service.core.weapp.ICoreWeappCloudService;
import com.niu.core.service.core.weapp.param.WeappUploadParam;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

@Service
public class CoreWeappCloudServiceImpl implements ICoreWeappCloudService {

    @Resource
    INiucloudService niucloudService;

    @Resource
    AddonMapper addonMapper;

    @Resource
    AddonInstallTools addonInstallTools;

    /**
     * 小程序上传
     *
     * @param param
     * @return
     */
    public String uploadWeapp(WeappUploadParam param) {
        String taskKey = String.valueOf(System.currentTimeMillis() / 1000);

        String tempDir = WebAppEnvs.get().webRootDownRuntime + "weapp_build/" + taskKey + "/";
        String packageDir = tempDir + "package/";
        FileTools.createDirs(packageDir);

        // 整理编译文件
        Addon compileAddon = addonMapper.selectOne(new QueryWrapper<Addon>().select("`key`").like("compile", "weapp").last("limit 1"));
        if (compileAddon == null) {
            handleUniapp(packageDir + "uni-app/",  param);
        } else {
            handleCompileWeapp(packageDir + "uni-app/", compileAddon.getKey(), param);
        }

        try {
            FileUtils.copyFile(new File(WebAppEnvs.get().webRootDownResource + param.getUploadPrivateKey()), new File(packageDir + "private.key"));
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }

        File zipFile = ZipUtil.zip(packageDir, tempDir + "build.zip");

        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> actionQuery = new HashMap<>();
        actionQuery.put("data[product_key]", instance.getProductKey());
        JSONObject actionToken = niucloudService.getActionToken("weappbuild", actionQuery);

        Map<String, Object> query = new HashMap<>();
        query.put("authorize_code", instance.getCode());
        query.put("compile", compileAddon != null ? 1 : 0);
        query.put("appid", param.getAppId());
        query.put("version", param.getVersion());
        query.put("desc", param.getDesc());
        query.put("do", 1);
        query.put("timestamp", taskKey);
        query.put("token", actionToken == null ? "" : actionToken.getStr("token"));

        HttpResponse response = new NiucloudUtils.Cloud().useThirdBuild().build("cloud/weapp").query(query)
            .func(i -> {
                i.form("file", zipFile, "build.zip");
            })
            .method(Method.POST).execute();

        JSONObject res =  JSONUtil.parseObj(response.body());

        if (!res.getInt("code", 0).equals(1)) throw new CommonException(res.getStr("msg"));

        return taskKey;
    }

    /**
     * 整理编译文件
     *
     * @param packageDir
     * @return
     */
    private void handleUniapp(String packageDir, WeappUploadParam param) {
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

            String content = FileUtils.readFileToString(envFile, "UTF-8");
            content = content.replace("VITE_APP_BASE_URL = ''", "VITE_APP_BASE_URL = '" + param.getBaseUrl() + "/api/" + "'");
            content = content.replace("VITE_IMG_DOMAIN = ''", "VITE_IMG_DOMAIN = '" + param.getBaseUrl() + "'");
            content = content.replace("VITE_SITE_ID = ''", "VITE_SITE_ID = '" + param.getSiteId().toString() + "'");
            FileUtil.writeUtf8String(content, envFile);

            addonInstallTools.handlePagesJson(packageDir, param.getAddon());
            addonInstallTools.handleUniappComponent(packageDir, param.getAddon());
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 整理编译文件
     *
     * @param packageDir
     * @param addon
     */
    private void handleCompileWeapp(String packageDir, String addon, WeappUploadParam param) {
        File CompileFile = new File(WebAppEnvs.get().webRootDownAddon + addon + "/weapp");
        if (!CompileFile.exists()) throw new CommonException("未找到微信小程序编译包");

        try {
            FileUtils.copyDirectory(CompileFile, new File(packageDir));

            File requestFile = new File(packageDir + "utils/request.js");
            if (requestFile.exists()) {
                String content = FileUtils.readFileToString(requestFile, "UTF-8");
                content = content.replace("{{$baseUrl}}", param.getBaseUrl() + "/api/");
                content = content.replace("{{$siteId}}", param.getSiteId().toString());
                FileUtil.writeUtf8String(content, requestFile);
            }

            File commonFile = new File(packageDir + "utils/common.js");
            if (commonFile.exists()) {
                String content = FileUtils.readFileToString(commonFile, "UTF-8");
                content = content.replace("{{$imgUrl}}", param.getBaseUrl());
                FileUtil.writeUtf8String(content, requestFile);
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 获取小程序编译日志
     *
     * @param key
     * @return
     */
    @Override
    public JSONObject getWeappCompileLog(String key) {
        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("authorize_code", instance.getCode());
        query.put("timestamp", key);

        HttpResponse response = new NiucloudUtils.Cloud().useThirdBuild().build("cloud/get_weapp_logs").query(query).method(Method.GET).execute();
        try {
            JSONObject res =  JSONUtil.parseObj(response.body());
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取微信小程序预览码
     *
     * @return
     */
    public String getWeappPreviewImage() {
        try {
            NiucloudUtils instance = NiucloudUtils.getInstance();

            Map<String, Object> query = new HashMap<>();
            query.put("authorize_code", instance.getCode());

            HttpResponse response = new NiucloudUtils.Cloud().useThirdBuild().build("cloud/get_weapp_preview").query(query).execute();
            if (JSONUtil.isJson(response.body()) && JSONUtil.parseObj(response.body()).getInt("code").equals("0")) return "";
            if (checkImageType(response.bodyStream()).equals("unknown")) return "";
            return "data:image/"+ checkImageType(response.bodyStream()) +";base64," + Base64.getEncoder().encodeToString(response.bodyBytes());
        } catch (Exception e) {
            return "";
        }
    }

    public static String checkImageType(InputStream is) throws Exception {
        byte[] header = new byte[8]; // Read the first 8 bytes
        if (is.read(header) != header.length) {
            throw new Exception("Unable to read enough data from input stream.");
        }

        ByteBuffer bb = ByteBuffer.wrap(header);

        if (bb.order() == ByteOrder.BIG_ENDIAN && bb.getInt(0) == 0x89504e47) {
            return "png";
        } else if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
            return "jpeg";
        } else if (header[0] == 'G' && header[1] == 'I' && header[2] == 'F') {
            return "gif";
        } else {
            return "unknown";
        }
    }
}
