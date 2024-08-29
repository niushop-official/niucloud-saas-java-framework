package com.niu.core.common.loader.notice;

import cn.hutool.json.JSONObject;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.SystemLoader;
import com.niu.core.common.loader.upload.BaseUpload;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class NoticeLoader extends SystemLoader {

    /**
     * 获取所有消息驱动
     * @return
     */
    public static JSONObject getType() {
        JSONObject jsonObject = JsonModuleLoader.build().mergeResultElement("notice/notice.json");
        return jsonObject;
    }

    /**
     * @param key
     * @return
     */
    public static BaseNotice getDriver(String key) {
        JSONObject jsonObject = NoticeLoader.getType();
        JSONObject keyJson = jsonObject.getJSONObject(key);
        BaseNotice base = BaseNotice.forName(keyJson.getStr("driver"));
        if (base == null) throw new CommonException("消息不存在");
        return base;
    }

}
