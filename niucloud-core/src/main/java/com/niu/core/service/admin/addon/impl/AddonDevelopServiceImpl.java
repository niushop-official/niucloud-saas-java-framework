package com.niu.core.service.admin.addon.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.utils.image.ImageUtils;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.service.admin.addon.IAddonDevelopService;
import com.niu.core.service.admin.addon.param.AddonDevelopAddParam;
import com.niu.core.service.admin.addon.param.AddonDevelopSearchParam;
import com.niu.core.service.admin.addon.vo.AddonDevelopInfoVo;
import com.niu.core.service.admin.addon.vo.AddonDevelopListVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 插件开发实现类
 */
@Service
public class AddonDevelopServiceImpl implements IAddonDevelopService {

    @Resource
    AddonMapper addonMapper;

    @Resource
    ICoreAddonService coreAddonService;

    public List<AddonDevelopListVo> list(AddonDevelopSearchParam searchParam) {
        List<AddonDevelopListVo> list = new LinkedList<>();

        try {
            // 获取已安装的插件
            Map<String, InstallAddonListVo> installAddonList = coreAddonService.getInstallAddonList();

            // 获取本地所有的插件
            List<File> localAddons = Files.list(Paths.get(WebAppEnvs.get().webRootDownAddon))
                    .map(path -> path.toFile())
                    .filter(file -> file.isDirectory())
                    .collect(Collectors.toList());

            for (File file : localAddons) {
                if (new File(file, "info.json").exists()) {
                    JSONObject info = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(new File(file, "info.json")));
                    String addon = info.getStr("key");
                    AddonDevelopListVo addonDevelopListVo = JSONUtil.toBean(info, AddonDevelopListVo.class);
                    if (installAddonList.get(addon) != null) addonDevelopListVo.setInstallInfo(installAddonList.get(addon));
                    addonDevelopListVo.setIcon(ImageUtils.imageToBase64(file + "/resource/icon.png"));
                    addonDevelopListVo.setCover(ImageUtils.imageToBase64(file + "/resource/cover.png"));
                    list.add(addonDevelopListVo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public AddonDevelopInfoVo info(String key) {
        File infoFile = new File(WebAppEnvs.get().webRootDownAddon + key + "/info.json");
        if (!infoFile.exists()) return null;

        JSONObject info = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(infoFile));
        AddonDevelopInfoVo addonDevelopInfoVo = JSONUtil.toBean(info, AddonDevelopInfoVo.class);

        return addonDevelopInfoVo;
    }

    /**
     * 添加插件
     * @param param
     */
    public void add(AddonDevelopAddParam param) {
        File infoFile = new File(WebAppEnvs.get().webRootDownAddon + param.getKey() + "/info.json");
        if (infoFile.exists()) throw new CommonException("已存在相同插件标识的应用");

        this.generateFile(param);
    }

    private void generateFile(AddonDevelopAddParam param) {
        try {
            File directory = new File(WebAppEnvs.get().webRootDownResource + "/static/tmpl/addon/");
            // 获取目录下所有的文件和子目录
            Collection<File> files = FileUtils.listFilesAndDirs(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

            String addonDir = WebAppEnvs.get().webRootDownAddon + param.getKey();
            FileTools.createDirs(addonDir);

            Map<String, String> replacements = new HashMap<>();
            replacements.put("{title}", param.getTitle());
            replacements.put("{desc}", param.getDesc());
            replacements.put("{key}", param.getKey());
            replacements.put("{pascalCaseKey}", StringUtils.toPascalCase(param.getKey())); // 大驼峰插件key
            replacements.put("{camelCasekey}", StringUtils.toCamelCase(param.getKey())); // 小驼峰插件key
            replacements.put("{version}", param.getVersion());
            replacements.put("{author}", param.getAuthor());
            replacements.put("{type}", param.getType());
            replacements.put("{supportApp}", param.getSupportApp());

            for (File file : files) {
                if (file.isDirectory()) {
                    String dir = file.getPath().replace(directory.getPath(), "");
                    FileTools.createDirs(this.replace(addonDir + dir, replacements));
                } else {
                    String content = this.replace(FileUtils.readFileToString(file, "UTF-8"), replacements);
                    String filePath = this.replace(addonDir + file.getPath().replace(directory.getPath(), ""), replacements);
                    FileUtils.writeStringToFile(new File(filePath), content, "UTF-8");
                }
            }

            generateImage(param.getIcon(), "icon", addonDir + "/resource/");
            generateImage(param.getCover(), "cover", addonDir + "/resource/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateImage(String path, String type, String dir) {
        try {
            String fileExt = "png";
            if (path.indexOf("http://") == -1 && path.indexOf("http://") == -1) {
                File file = new File(WebAppEnvs.get().webRootDownResource + path);
                if (file.exists()) {
                    FileUtils.copyFile(file, new File(dir, type + "." + fileExt));
                }
            } else {
                URL url = new URL(path);
                BufferedImage image = ImageIO.read(url.openStream());
                ImageIO.write(image, fileExt, new File(dir, type + "." + fileExt));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String replace(String content, Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            content = content.replaceAll(Pattern.quote(entry.getKey()), Matcher.quoteReplacement(entry.getValue()));
        }
        return content;
    }

    public void edit(AddonDevelopAddParam param) {
        File infoFile = new File(WebAppEnvs.get().webRootDownAddon + param.getKey() + "/info.json");
        if (!infoFile.exists()) throw new CommonException("插件不存在");

        this.generateFile(param);

        Addon addon = coreAddonService.getInfoByKey(param.getKey());
        if (addon != null) {
            Addon model = new Addon();
            model.setTitle(param.getTitle());
            model.setVersion(param.getVersion());
            model.setDesc(param.getDesc());
            model.setIcon(param.getIcon());
            model.setKey(param.getKey());
            model.setType(param.getType());
            model.setSupportApp(param.getSupportApp());
            coreAddonService.set(model);
        }
    }

    /**
     * 删除插件
     * @param key
     */
    public void del(String key) {
        File infoFile = new File(WebAppEnvs.get().webRootDownAddon + key + "/info.json");
        if (!infoFile.exists()) throw new CommonException("插件不存在");

        Addon addon = coreAddonService.getInfoByKey(key);
        if (addon != null) throw new CommonException("已安装的插件不允许删除");

        try {
            FileUtils.deleteDirectory(new File(WebAppEnvs.get().webRootDownAddon + key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
