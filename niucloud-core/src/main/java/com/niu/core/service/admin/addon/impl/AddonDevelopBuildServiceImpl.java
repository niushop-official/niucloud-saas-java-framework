package com.niu.core.service.admin.addon.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.mapper.sys.SysMenuMapper;
import com.niu.core.service.admin.addon.IAddonDevelopBuildService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 插件日志实现类
 */
@Service
public class AddonDevelopBuildServiceImpl implements IAddonDevelopBuildService {

    @Resource
    SysMenuMapper sysMenuMapper;

    private String addon;

    /** 编译目录 */
    private String addonPath;

    /**
     * 插件打包
     */
    @Override
    public void build(String addon) {
        if (!GlobalConfig.runActive.equals("dev")) throw new CommonException("只有在开发环境下才可以进行打包操作");

        if (!new File(WebAppEnvs.get().projectNiucloudAddon + addon).exists()) throw new CommonException("插件不存在");
        File infoFile = new File(WebAppEnvs.get().projectNiucloudAddon + addon + "/src/main/resources/info.json");
        if (!infoFile.exists()) throw new CommonException("插件不存在");

        this.addon = addon;
        this.addonPath = WebAppEnvs.get().webRootDownAddon + addon + "/";

        try {
            for (File child : new File(this.addonPath).listFiles()) {
                if (child.isDirectory() && !child.getName().equals("sql")) FileUtils.cleanDirectory(child);
            }
            FileUtils.copyFile(infoFile, new File(this.addonPath + "info.json"));
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }

        this.admin();
        this.web();
        this.uniapp();
        this.java();
        this.jar();
        this.resource();
        this.buildUniappLangJson();
        this.buildUniappPagesJson();
        this.menu("site");
        this.menu("admin");
        // 生成压缩包
        this.compressor();
    }

    private void admin() {
        try {
            File sourcePath = new File(WebAppEnvs.get().projectRoot + "/admin/src/addon/" + this.addon + "/");
            if (!sourcePath.exists()) return;

            File destPath = new File(this.addonPath + "admin/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }
            FileUtils.copyDirectory(sourcePath, destPath);

            // 打包admin icon文件
            File iconDir = new File(WebAppEnvs.get().projectRoot + "/admin/src/styles/icon/addon/" + this.addon + "/");
            if (iconDir.exists()) {
                FileUtils.copyDirectory(iconDir, new File(destPath, "icon"));
            }
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void uniapp() {
        try {
            File sourcePath = new File(WebAppEnvs.get().projectRoot + "/uni-app/src/addon/" + this.addon + "/");
            if (!sourcePath.exists()) return;

            File destPath = new File(this.addonPath + "uni-app/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }

            FileUtils.copyDirectory(sourcePath, destPath);
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void web() {
        try {
            File sourcePath = new File(WebAppEnvs.get().projectRoot + "/web/addon/" + this.addon + "/");
            if (!sourcePath.exists()) return;

            File destPath = new File(this.addonPath + "web/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }
            FileUtils.copyDirectory(sourcePath, destPath);

            // 拷贝layouts
            File layoutDir = new File(WebAppEnvs.get().projectRoot + "/web/layouts/" + this.addon + "/");
            if (layoutDir.exists()) {
                File layoutDirDestPath = new File(destPath, "layouts/" + this.addon);
                if (layoutDirDestPath.exists()) {
                    FileUtils.cleanDirectory(layoutDirDestPath);
                } else {
                    FileTools.createDirs(layoutDirDestPath.getPath());
                }
                FileUtils.copyDirectory(layoutDir, layoutDirDestPath);
            }
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void java() {
        try {
            File sourcePath = new File(WebAppEnvs.get().projectNiucloudAddon + this.addon + "/");
            if (!sourcePath.exists()) return;

            File destPath = new File(this.addonPath + "java/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }

            String[] exclusionDir = {".mvm", "target"};
            FileUtils.copyDirectory(sourcePath, destPath, FileTools.createExclusionFilter(exclusionDir));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void jar() {
        File sourcePath = new File(WebAppEnvs.get().projectNiucloudAddon + this.addon + "/target/" + this.addon + ".jar");
        if (!sourcePath.exists()) return;

        try {
            File destPath = new File(this.addonPath + "jar/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }
            FileUtils.copyFile(sourcePath, new File(destPath, this.addon + ".jar"));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void resource() {
        try {
            File sourcePath = new File(WebAppEnvs.get().webRoot + "resource/addon/" + this.addon + "/");
            if (!sourcePath.exists()) return;

            File destPath = new File(this.addonPath + "resource/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }

            FileUtils.copyDirectory(sourcePath, destPath);
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void buildUniappPagesJson() {
        File mainPages = new File(WebAppEnvs.get().projectRoot + "/uni-app/src/pages.json");
        if (!mainPages.exists()) return;

        try {
            String content = FileUtils.readFileToString(mainPages, "UTF-8");

            Pattern pattern = Pattern.compile("//\\s*"+ (this.addon.toUpperCase() + "_PAGE_BEGIN") +"(.*?)//\\s*" + (this.addon.toUpperCase() + "_PAGE_END"), Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String pagesContent = matcher.group(1).trim();
                String packageDir = this.addonPath + "package/";
                FileTools.createDirs(packageDir);
                FileUtils.writeStringToFile(new File(packageDir, "uni-app-pages.txt"), pagesContent, "UTF-8");
            }
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void buildUniappLangJson() {
        try {
            File zh = new File(WebAppEnvs.get().projectRoot + "/uni-app/src/locale/zh-Hans.json");
            File en = new File(WebAppEnvs.get().projectRoot + "/uni-app/src/locale/en.json");
            JSONObject zhJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(zh));
            JSONObject enJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(en));

            File addonZh = new File(this.addonPath +"uni-app/locale/zh-Hans.json");
            File addonEn = new File(this.addonPath + "uni-app/locale/en.json");

            JSONObject newZhJson = new JSONObject();
            for (String key : zhJson.keySet()) {
                if (key.indexOf(this.addon + ".") == 0) {
                    newZhJson.put(key.replace(this.addon + ".", ""), zhJson.getStr(key));
                }
            }
            JSONObject newEnJson = new JSONObject();
            for (String key : enJson.keySet()) {
                if (key.indexOf(this.addon + ".") == 0) {
                    newEnJson.put(key.replace(this.addon + ".", ""), zhJson.getStr(key));
                }
            }

            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newZhJson), addonZh);
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newEnJson), addonEn);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void menu(String appType) {
        String menuDir = this.addonPath + "menu/";
        FileTools.createDirs(menuDir);

        File menuFile = new File(menuDir, appType + ".json");
        JSONObject menuJson = new JSONObject();

        SysMenu model = new SysMenu();
        model.setSource("system");
        sysMenuMapper.update(model, new QueryWrapper<SysMenu>().ne("source", "system").eq("app_type", appType).eq("addon", this.addon));

        List<SysMenu> menuList = sysMenuMapper.selectList(new QueryWrapper<SysMenu>().select("menu_name,menu_key,menu_short_name,parent_select_key,parent_key,menu_type,icon,api_url,router_path,view_path,methods,sort,status,is_show").eq("app_type", appType).eq("addon", this.addon));
        JSONArray menuTree = TreeUtils.listToTree(JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(menuList)), "menu_key", "parent_key", "children");
        menuJson.put("menu", menuTree);

        if (menuFile.exists()) {
            JSONObject original = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(menuFile));
            menuJson.put("delete", original.getJSONArray("delete") != null ? original.getJSONArray("delete") : new JSONArray());
        } else {
            menuJson.put("delete", new JSONArray());
        }
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(menuJson), menuFile);

        File addonMenuDir = new File(WebAppEnvs.get().projectNiucloudAddon + this.addon + "/src/main/resources/" + addon + "/loader/menu/");
        if (addonMenuDir.exists()) {
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(menuJson), new File(addonMenuDir, appType + ".json"));
        }
    }

    private void compressor() {
        String tempDir = WebAppEnvs.get().webRootDownResource + "temp/" + this.addon + "/" + this.addon;
        FileTools.createDirs(tempDir);

        try {
            FileUtils.copyDirectory(new File(this.addonPath), new File(tempDir));

            String sourceDir = WebAppEnvs.get().webRootDownResource + "temp/" + this.addon;
            String zipFile =  WebAppEnvs.get().webRootDownResource + "temp/" + this.addon + ".zip";

            ZipUtil.zip(sourceDir, zipFile);
            FileUtils.deleteDirectory(new File(sourceDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载插件
     *
     * @param key
     * @return
     */
    public String download(String key) {
        File file = new File(WebAppEnvs.get().webRootDownResource + "temp/" + key + ".zip");
        if (!file.exists()) throw new CommonException("请先打包插件");
        return file.getPath().replace(WebAppEnvs.get().projectRoot, "");
    }
}

