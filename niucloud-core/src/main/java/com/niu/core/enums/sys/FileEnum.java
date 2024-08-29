package com.niu.core.enums.sys;

/**
 * 云存储枚举类
 */
public enum FileEnum {

    IMAGE("图片", "image"),
    VIDEO("视频", "video"),
    DOCUMENT("文件","document"),
    LOCAL("本地存储", "local"),
    QINIU("七牛云存储", "qiniu"),
    ALIYUN("阿里云存储", "aliyun"),
    QCLOUD("腾讯云存储", "qcloud"),
    WECHAT("微信公众号", "wechat"),
    APPLET("小程序", "applet"),
    BIG("微信支付", "big"),
    mid("小程序上传", "mid"),
    SMALL("", "small")
    ;
    String name;
    String code;

    FileEnum(String name, String code){
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
