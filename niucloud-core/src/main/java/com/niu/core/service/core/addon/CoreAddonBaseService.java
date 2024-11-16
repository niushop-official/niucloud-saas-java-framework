package com.niu.core.service.core.addon;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.common.config.GlobalConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CoreAddonBaseService {

    /** 当前运行模式 */
    protected String runActive;

    /** 开发环境运行根目录 */
    protected String devRootPath;

    /** 开发环境插件根目录 */
    protected String devAddonPath;

    /** 插件下载目录 */
    protected String addonDownloadPath;

    /** 编译运行根目录 */
    protected String webRootPath;

    /** 前端编译目录 */
    protected String webPublicPath;

    /** 前端原始代码复制目录 */
    protected String webRuntimePath;

    /** 运行jar目录 */
    protected String webJarPath;

    public CoreAddonBaseService(){
        this.runActive = GlobalConfig.runActive;
        this.devRootPath = WebAppEnvs.get().projectRoot;
        this.devAddonPath = this.devRootPath + "/niucloud-addon";
        this.webRootPath = this.devRootPath + "/webroot";
        this.addonDownloadPath = this.webRootPath + "/addon";
        this.webPublicPath = this.webRootPath + "/public";
        this.webRuntimePath = this.webRootPath + "/runtime";
        this.webJarPath = this.webRootPath + "/jar";
    }

    public Map<String, String> getPath(){
        Map<String, String> map = new HashMap<>();
        map.put("devRootPath", this.devAddonPath);
        map.put("devAddonPath", this.devAddonPath);
        map.put("webRootPath", this.webRootPath);
        map.put("addonDownloadPath", this.addonDownloadPath);
        map.put("webPublicPath", this.webPublicPath);
        map.put("webRuntimePath", this.webRuntimePath);
        map.put("webJarPath", this.webJarPath);
        return map;
    }

    /**
     * 获取插件基础配置信息
     * @param addon
     * @return
     */
    public JSONObject getAddonConfig(String addon) {
        File infoFile = new File(WebAppEnvs.get().webRootDownAddon + "/" + addon + "/info.json");
        if (!infoFile.exists()) throw new CommonException("插件info.json文件不存在");

        return JSONUtil.parseObj(JsonLoadUtils.loadJsonString(infoFile));
    }
}
