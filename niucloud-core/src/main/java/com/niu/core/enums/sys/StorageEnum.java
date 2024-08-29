package com.niu.core.enums.sys;

/**
 * 云存储枚举类
 */
public enum StorageEnum {

    LOCAL("本地存储", "local"),
    QINIU("七牛云存储", "qiniu"),
    ALI("阿里云存储", "aliyun"),
    TENCENT("腾讯云存储", "tencent"),
    ON("开启", "1"),
    OFF("关闭", "0")
    ;
    String name;
    String code;

    StorageEnum(String name, String code){
        this.name=name;
        this.code=code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
