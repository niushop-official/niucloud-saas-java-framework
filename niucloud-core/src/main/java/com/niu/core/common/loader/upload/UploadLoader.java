package com.niu.core.common.loader.upload;

import cn.hutool.json.JSONObject;
import com.niu.core.common.loader.SystemLoader;
import com.niu.core.common.utils.json.JsonModuleLoader;

import java.io.File;

public class UploadLoader extends SystemLoader {

    /**
     * 获取所有上传驱动
     *
     * @return
     */
    public static JSONObject getType() {
        JSONObject jsonObject = JsonModuleLoader.build().mergeResultElement("upload.json");
        return jsonObject;
    }

    /**
     * @param key
     * @return
     */
    public static BaseUpload getDriver(String key, JSONObject config) {
        JSONObject jsonObject = UploadLoader.getType();
        JSONObject keyJson = jsonObject.getJSONObject(key);
        return BaseUpload.forName(keyJson.getStr("driver"), config);
    }

}
