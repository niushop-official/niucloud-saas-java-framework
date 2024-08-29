package com.niu.core.common.loader.upload;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * 基础上传类
 */
public abstract class BaseUpload {

    protected JSONObject config;

    public BaseUpload(JSONObject config) {
        this.config = config;
    }

    /**
     * @param driver
     * @return
     */
    public static BaseUpload forName(String driver, JSONObject config) {
        BaseUpload baseUpload = null;
        try {
            Class<?> clazz = Class.forName(driver);
            Constructor<?> constructor = clazz.getConstructor(JSONObject.class);
            baseUpload = (BaseUpload) constructor.newInstance(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseUpload;
    }

    /**
     *
     * @param filePath
     * @return
     */
    public String getUrl(String filePath) {
        if (this.config == null) return filePath;
        String domain = this.config.getStr("domain");
        if (domain != null && !domain.isEmpty()) {
            return domain + "/" + filePath;
        }
        return filePath;
    }

    /**
     * 上传
     *
     */
    public abstract void upload(AttachmentUploadParam param);

    /**
     * 删除文件
     * @param filePath
     */
    public abstract void delete(String filePath);

    /**
     * 图片缩略
     * @param filePath
     * @param thumbType
     * @return
     */
    public abstract Map<String, String> thumb(String filePath, String thumbType);
}
