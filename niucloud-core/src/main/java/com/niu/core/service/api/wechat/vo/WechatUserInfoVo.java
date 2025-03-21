package com.niu.core.service.api.wechat.vo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

@Data
public class WechatUserInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String avatar = "";
    private String nickname = "";
    private String openid = "";
    private String unionid = "";

}
