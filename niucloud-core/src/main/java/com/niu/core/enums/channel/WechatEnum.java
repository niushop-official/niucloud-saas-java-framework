package com.niu.core.enums.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信相关枚举
 */
@Getter
@AllArgsConstructor
public enum WechatEnum {
    /************************************************* 请求消息的属性 *****************************************/

    MESSAGE_TYPE_EVENT("event"),//请求消息的属性  事件
    MESSAGE_TYPE_TEXT("text"),//请求消息的属性  文本
    MESSAGE_TYPE_IMAGE("image"),//请求消息的属性  图片
    MESSAGE_TYPE_VOICE("voice"),//请求消息的属性  语音
    MESSAGE_TYPE_VIDEO("video"),//请求消息的属性  视频
    MESSAGE_TYPE_LOCATION("location"),//请求消息的属性  坐标
    MESSAGE_TYPE_LINK("link"),//请求消息的属性  链接
    MESSAGE_TYPE_FILE("file"),//请求消息的属性  文件

    /************************************************* 请求事件的类型 *****************************************/

    EVENT_SUBSCRIBE("subscribe"),//事件类型 - 关注
    EVENT_SCAN("SCAN"),

    /************************************************* 回复的类型 *****************************************/

    REPLY_SUBSCRIBE("subscribe"),
    REPLY_DEFAULT("default"),
    REPLY_KEYWORD("keyword"),

    /************************************************* 消息加解密方式    not_encrypt 明文   compatible 兼容  safe 安全 *****************************************/

    NOT_ENCRYPT("not_encrypt"),//明文
    COMPATIBLE("compatible"),//兼容
    SAFE("safe"),//safe

    ;
    private final String name;
}
