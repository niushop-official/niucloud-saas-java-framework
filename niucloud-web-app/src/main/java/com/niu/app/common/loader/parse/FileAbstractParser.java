package com.niu.app.common.loader.parse;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class FileAbstractParser extends AbstractParser {

    // 装载的路径
    private static String pluginName = "addon";

    public FileAbstractParser(String home) throws Exception {
        super(home);
        try {
            String pluginPath = home + "/" + pluginName + "/";
            log.info("运行时插件路径：" + pluginPath);
            String pluginConfigPath = pluginPath + pluginName + ".json";
            log.info("运行时插件配置文件路径：" + pluginConfigPath);
            File pluginConfigFile = new File(pluginConfigPath);
            if (!pluginConfigFile.exists()) {
                return;
            }
            String pluginConfigContent = readToContent(pluginConfigFile);
            JSONArray jsonArray = JSONUtil.parseArray(pluginConfigContent);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getStr("name");
                    String jar = jsonObject.getStr("jar");
                    String mainClass = jsonObject.getStr("mainClass");
                    String jarPath = pluginPath + "/" + jar;
                    addonMap.put(name, mainClass);
                    jarMap.put(name, jarPath);
                }
            }
        } catch (Exception e) {
            throw new Exception("解析插件配置文件异常.", e.getCause());
        }
    }

}
