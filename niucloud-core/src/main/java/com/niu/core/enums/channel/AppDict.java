package com.niu.core.enums.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

/**
 * app相关枚举类
 */
public class AppDict {

    /**
     * 平台类型枚举
     */
    @Getter
    @AllArgsConstructor
    public enum PlatformEnum {
        ANDROID("android", "Android");
//        IOS("ios", "iOS");

        private final String value;
        private final String desc;
    }

    /**
     * 版本状态枚举
     */
    @Getter
    @AllArgsConstructor
    public enum StatusEnum {
        STATUS_UPLOAD_SUCCESS("upload_success", "上传成功"),
        STATUS_CREATE_FAIL("create_fail", "创建失败"),
        STATUS_PUBLISHED("published", "发布成功"),
        STATUS_CREATING("creating", "创建中");

        private final String value;
        private final String desc;
    }

    /**
     * 获取平台类型map
     */
    public static Map<String, Object> getPlatformMap() {
        Map<String, Object> map = new HashMap<>();
        for (PlatformEnum platform : PlatformEnum.values()) {
            map.put(platform.getValue().toString(), platform.getDesc());
        }
        return map;
    }

    /**
     * 根据值获取平台类型名称
     */
    public static String getPlatformName(String value) {
        if (value == null) {
            return "";
        }
        for (PlatformEnum platform : PlatformEnum.values()) {
            if (platform.getValue().equals(value)) {
                return platform.getDesc();
            }
        }
        return "";
    }

    /**
     * 获取状态map
     */
    public static Map<String, Object> getStatusMap() {
        Map<String, Object> map = new HashMap<>();
        for (StatusEnum status : StatusEnum.values()) {
            map.put(status.getValue().toString(), status.getDesc());
        }
        return map;
    }

    /**
     * 根据值获取状态名称
     */
    public static String getStatusName(String value) {
        if (value == null) {
            return "";
        }
        for (StatusEnum status : StatusEnum.values()) {
            if (status.getValue().equals(value)) {
                return status.getDesc();
            }
        }
        return "";
    }
}