package com.niu.core.enums.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信相关枚举
 */
@Getter
@AllArgsConstructor
public enum WechatReplyTypeEnum {

    REPLY_SUBSCRIBE("subscribe"),
    REPLY_DEFAULT("default"),
    REPLY_KEYWORD("keyword")
    ;

    private final String type;
}
