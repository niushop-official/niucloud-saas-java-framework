package com.niu.core.enums.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信相关枚举
 */
@Getter
@AllArgsConstructor
public enum WechatMediaTypeEnum {

    IMAGE("image", "图片"),
    VOICE("voice", "音频"),
    VIDEO("video", "视频"),
    NEWS("news", "图文")
    ;

    private final String type;

    private final String name;
}
