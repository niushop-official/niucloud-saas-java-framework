package com.niu.core.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

import java.net.MalformedURLException;
import java.net.URL;

public class CommonUtils {
    /**
     * 判断字符串是否是链接
     * @param str
     * @return
     */
    public static boolean isUrl(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            new URL(str);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * 全局唯一ID
     * @return
     */
    public static String createNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }
}
