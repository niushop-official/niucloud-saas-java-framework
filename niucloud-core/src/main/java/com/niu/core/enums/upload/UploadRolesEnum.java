package com.niu.core.enums.upload;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 上传规范枚举
 */
@Getter
@AllArgsConstructor
public enum UploadRolesEnum {
    IMAGE("image",new String[]{"jpg", "jpeg", "png", "gif"}, new String[]{"image/jpeg", "image/gif", "image/png"}, 10485760L),
    VIDEO("video", new String[]{"mp4"}, new String[]{"video/mp4"}, 104857600L),
    WECHAT("wechat", new String[]{"pem", "key"}, new String[]{"application/x-x509-ca-cert", "application/octet-stream", "application/x-iwork-keynote-sffkey"}, 1048576l),
    ALIYUN("aliyun", new String[]{"crt"}, new String[]{"application/x-x509-ca-cert", "application/octet-stream"}, 10485760l),
    APPLET("applet", new String[]{"zip", "rar"}, new String[]{"application/zip", "application/vnd.rar", "application/x-zip-compressed"}, 104857600L),
    ;

    /** 规则名称 */
    private final String name;

    /** 扩展名 */
    private final String[] ext;

    /** 文件类型 */
    private final String[] mime;

    /** 上传大小 */
    private final Long size;


}
