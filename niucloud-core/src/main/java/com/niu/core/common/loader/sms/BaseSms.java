package com.niu.core.common.loader.sms;

import cn.hutool.json.JSONObject;
import com.niu.core.common.loader.notice.NoticeDataVo;
import cn.hutool.core.util.ClassLoaderUtil;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * 基础上传类
 */
public abstract class BaseSms {

    protected JSONObject config;

    public BaseSms(JSONObject config) {
        this.config = config;
    }

    /**
     * @param driver
     * @return
     */
    public static BaseSms forName(String driver, JSONObject config) {
        BaseSms base = null;
        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(driver);
            Constructor<?> constructor = clazz.getConstructor(JSONObject.class);
            base = (BaseSms) constructor.newInstance(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base;
    }

    /**
     * 发送短信
     */
    public abstract SendResultVo send(NoticeInfoVo notice, NoticeDataVo noticeData);
}
