package com.niu.core.service.core.addon;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.io.Files;
import com.niu.core.component.context.SpringContext;
import com.niu.core.component.context.WebAppEnvs;
import com.niu.core.common.utils.file.AddonUpgradeHelper;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.utils.file.PipeNameUtils;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.common.utils.system.LocalMavenTools;
import com.niu.core.common.utils.system.RuntimeTools;
import com.niu.core.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AddonInstallJavaTools {

    /**
     * 用于测试
     *
     * @param addonName
     */
    public static void installExec(String addonName) {
        ICoreAddonInstallService iCoreAddonInstallService = (ICoreAddonInstallService) SpringContext.getBean(ICoreAddonInstallService.class);
        installExec((CoreAddonBaseService) iCoreAddonInstallService, addonName, new ArrayList<>());
    }

    /**
     * @param coreAddonBaseService
     * @param addonName
     */
    public static void installExec(CoreAddonBaseService coreAddonBaseService, String addonName, ArrayList<String> tips) {
        if (GlobalConfig.runActive.equalsIgnoreCase("dev")) {
            // 开发环境
            WebAppEnvs webAppEnvs = WebAppEnvs.get();
            String webRootDownAddon = webAppEnvs.projectNiucloudAddon;
            String addonPomPath = webRootDownAddon + '/' + addonName + "/pom.xml";
            // 存在pom.xml，说明是源码安装
            if (LocalMavenTools.existPomXmlFile(addonPomPath)) {
                // ==========================================================================================//
                // 源码安装
                // ==========================================================================================//
                // 1、判断是否已经安装
                String addonNameProjectPomXml = webAppEnvs.projectNiucloudAddon + "/" + addonName + "/pom.xml";
                File addonNameProjectPomXmlFile = new File(addonNameProjectPomXml);
                if (addonNameProjectPomXmlFile.isFile() && addonNameProjectPomXmlFile.exists()) {
                    // throw new RuntimeException("插件:" + addonName + ",已源码安装.");
                }
                // 2、源码已复制
                // ............................................................
                // 3、niucloud-addon模块下添加模块依赖
                String addonProject = webAppEnvs.projectNiucloudAddon;
                LocalMavenTools.addPomModuleNode(addonProject, addonName);
                log.info("已添加maven依赖.");
                tips.add("已添加maven依赖.");
            } else {
                // ==========================================================================================//
                // JAR包安装
                // ==========================================================================================//
                // 1、判断是否已经安装
                String addonNameProjectJar = webAppEnvs.projectNiucloudAddon + "/" + addonName + "/" + addonName + ".jar";
                File addonNameProjectJarFile = new File(addonNameProjectJar);
                if (addonNameProjectJarFile.isFile() && addonNameProjectJarFile.exists()) {
                    throw new RuntimeException("插件:" + addonName + ",已Jar方式安装.");
                }
                // 2、JAR安装，把JAR装入本地Maven仓库
                String addonJarPath = webAppEnvs.webRootDownAddon + '/' + addonName + "/java/" + addonName + ".jar";
                String pomEntry = LocalMavenTools.readPomXmlEntryFromJar(addonJarPath);
                if (StrUtil.isEmpty(pomEntry)) {
                    throw new RuntimeException("插件" + addonName + ".jar文件不存在.");
                }
                Map<String, String> coordinateMap = LocalMavenTools.readPomXmlCoordinate(addonJarPath, pomEntry);
                log.info("coordinateMap = " + coordinateMap);
                try {
                    log.warn("安装到maven到本地仓库，需要配置：MAVEN_HOME环境变量.");
                    LocalMavenTools.addLocalRepository(addonJarPath, coordinateMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 3、niucloud-addon模块下添加依赖
                String addonProject = webAppEnvs.projectNiucloudAddon;
                LocalMavenTools.addPomDependencyNode(addonProject, coordinateMap);
                // 4、复制一份JAR包到niucloud-addon项目下
                String targetAddonJarDir = webAppEnvs.projectNiucloudAddon + "/" + addonName;
                File targetAddonJarDirFile = new File(targetAddonJarDir);
                if (targetAddonJarDirFile.exists()) {
                    targetAddonJarDirFile.mkdirs();
                }
                String targetAddonJarPath = webAppEnvs.projectNiucloudAddon + "/" + addonName + '/' + addonName + ".jar";
                try {
                    Files.copy(new File(addonJarPath), new File(targetAddonJarPath));
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("复制" + addonName + ".jar文件错误.", e);
                }
            }
        } else {
            // 生产环境
            // 1、读取JAR内容
            String addonJarPath = RuntimeTools.getWebRootPath() + "/jar/addon/" + addonName + ".jar";
            File addonJarFile = new File(addonJarPath);
            if (!addonJarFile.exists()) {
                throw new RuntimeException("插件" + addonName + ".jar文件不存在.");
            }
            // 2、读取JAR中的描述文件
            JSONObject addonInfoJsonObject = JsonLoadUtils.readJsonObjectFromJarInner(addonJarPath, "info.json");
            if (addonInfoJsonObject == null) {
                throw new RuntimeException("插件" + addonName + ".jar文件中描述文件info.json不存在.");
            }
            String addonJar = addonInfoJsonObject.getStr("jar");
            String addonMainClass = addonInfoJsonObject.getStr("mainClass");
            // 3、插件归档目录下读取==>addon.json文件，文件可以为空
            String addonRunDescPath = RuntimeTools.getWebRootPath() + "/jar/addon/addon.json";
            JSONArray runAddonJsonArray = JsonLoadUtils.readFileToJSONArray(addonRunDescPath);
            boolean containedAddon = checkContainedAddon(runAddonJsonArray, addonName);
            if (containedAddon) {
                throw new RuntimeException("插件" + addonName + "已存在,不可以再次安装.");
            }
            // 4、添加到插件内容到：addon.json
            JSONObject newRunJsonObject = new JSONObject();
            newRunJsonObject.put("name", addonName);
            newRunJsonObject.put("jar", addonJar);
            newRunJsonObject.put("mainClass", addonMainClass);
            runAddonJsonArray.add(newRunJsonObject);
            String formattedJsonStr = JSONUtil.formatJsonStr(JSONUtil.toJsonStr(runAddonJsonArray));
            FileTools.write(addonRunDescPath, formattedJsonStr);
            // 6、保存当前安装信息
            AddonUpgradeHelper.cacheAddonInfo(tips);
            // 7、通过命名管道重启
            PipeNameUtils.noticeBootRestart(GlobalConfig.applicationName, addonName);
            log.warn("应用：" + GlobalConfig.applicationName + "，通过命名管道安装中......");
        }
    }

    /**
     * @param jsonArray
     * @param addonName
     * @return
     */
    private static boolean checkContainedAddon(JSONArray jsonArray, String addonName) {
        boolean contained = false;
        if (jsonArray == null || jsonArray.size() <= 0) {
            return contained;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getStr("name");
            if (StrUtil.isNotEmpty(name) && name.equals(addonName)) {
                contained = true;
            }
        }
        return contained;
    }

    /**
     * @param coreAddonBaseService
     * @param addonName
     */
    public static void uninstallExec(CoreAddonBaseService coreAddonBaseService, String addonName) {
        if (GlobalConfig.runActive.equalsIgnoreCase("dev")) {
            WebAppEnvs webAppEnvs = WebAppEnvs.get();
            // 判断项目下是源码还是JAR
            String projectAddonPomXmlPath = webAppEnvs.projectNiucloudAddon + "/" + addonName + "/pom.xml";
            File projectAddonPomXmlFile = new File(projectAddonPomXmlPath);
            if (projectAddonPomXmlFile.isFile() && projectAddonPomXmlFile.exists()) {
                // ==========================================================================================//
                // 源码安装进行卸载
                // ==========================================================================================//
                // 1、、Maven卸载依赖
                String addonProject = webAppEnvs.projectNiucloudAddon + "/" + addonName;
                LocalMavenTools.removePomModuleNode(addonProject, addonName);
                // 3、删除模块的所有项目文件（如果存在）
                String addonProjectPath = webAppEnvs.projectNiucloudAddon + "/" + addonName + "/";
                try {
                    File addonProjectFile = new File(addonProjectPath);
                    if (addonProjectFile.isDirectory() && addonProjectFile.exists()) {
                        FileUtils.deleteDirectory(addonProjectFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("删除项目插件目录：" + addonName + " 错误.");
                }
            } else {
                // ==========================================================================================//
                // JAR包安装进行卸载
                // ==========================================================================================//
                // 1、从项目Maven卸载仓库安装的Jar
                Map<String, String> coordinateMap = new HashMap<>();
                coordinateMap.put("artifactId", addonName);
                String addonProject = webAppEnvs.projectNiucloudAddon;
                LocalMavenTools.removePomDependencyNode(addonProject, coordinateMap);
                log.info("已删除maven依赖.");
                // 2、删除项目模块下的JAR
                String projectAddonJarPath = webAppEnvs.projectNiucloudAddon + "/" + addonName + '/' + addonName + ".jar";
                File projectAddonJarFile = new File(projectAddonJarPath);
                try {
                    projectAddonJarFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("删除插件模块：" + addonName + ".jar错误.");
                }
                // 3、删除模块文件
                try {
                    File parentFile = projectAddonJarFile.getParentFile();
                    if (parentFile.isDirectory() && parentFile.exists()) {
                        FileUtils.deleteDirectory(parentFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("删除插件模块：" + addonName + " 错误.");
                }
            }
        } else {
            WebAppEnvs webAppEnvs = WebAppEnvs.get();
            // 1、判断插件的JAR是否存在
            String addonJarPath = webAppEnvs.webRootDownJar + "/addon/" + addonName + ".jar";
            File addonJarFile = new File(addonJarPath);
            try {
                if (addonJarFile.exists()) {
                    addonJarFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 2、重写插件描述文件，使得重启不加载
            String addonRunDescPath = webAppEnvs.webRootDownJar + "/addon/addon.json";
            JSONArray runAddonJsonArray = JsonLoadUtils.readFileToJSONArray(addonRunDescPath);
            runAddonJsonArray = removeContainedAddon(runAddonJsonArray, addonName);
            String formattedJsonStr = JSONUtil.formatJsonStr(JSONUtil.toJsonStr(runAddonJsonArray));
            FileTools.write(addonRunDescPath, formattedJsonStr);
            // 3、通过命名管道重启本服务（重启服务会删除本服务进程占用的JAR）
            PipeNameUtils.noticeBootRestartByUninstall(GlobalConfig.applicationName, addonName);
            log.warn("应用：" + GlobalConfig.applicationName + "，通过命名管道安装中......");
        }

    }

    /**
     * @param runAddonJsonArray
     * @param addonName
     * @return
     */
    private static JSONArray removeContainedAddon(JSONArray runAddonJsonArray, String addonName) {
        if (runAddonJsonArray == null || runAddonJsonArray.size() <= 0) {
            return runAddonJsonArray;
        }
        int index = 0;
        for (int i = 0; i < runAddonJsonArray.size(); i++) {
            JSONObject jsonObject = runAddonJsonArray.getJSONObject(i);
            String name = jsonObject.getStr("name");
            if (name.equals(addonName)) {
                index = i;
            }
        }
        runAddonJsonArray.remove(index);
        return runAddonJsonArray;
    }

}
