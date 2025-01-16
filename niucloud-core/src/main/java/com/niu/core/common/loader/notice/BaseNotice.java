package com.niu.core.common.loader.notice;

import cn.hutool.json.JSONObject;
import cn.hutool.core.util.ClassLoaderUtil;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * 消息模板类
 */
public abstract class BaseNotice {

    /**
     * @param driver
     * @return
     */
    public static BaseNotice forName(String driver) {
        BaseNotice base = null;
        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(driver);
            base = (BaseNotice) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base;
    }

    /**
     * 获取消息模板数据
     */
    public abstract NoticeDataVo noticeData(Map<String, Object> vars);
}
