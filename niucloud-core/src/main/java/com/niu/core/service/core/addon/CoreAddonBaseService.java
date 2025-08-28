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

    public Object getPath() {
        return null;
    }

    /**
     * 获取插件基础配置信息
     * @param addon
     * @return
     */
    public JSONObject getAddonConfig(String addon) {
        File infoFile = new File(WebAppEnvs.get().webRootDownAddon + "/" + addon + "/info.json");
        if (!infoFile.exists()) throw new CommonException(addon + "插件info.json文件不存在");

        return JSONUtil.parseObj(JsonLoadUtils.loadJsonString(infoFile));
    }
}
