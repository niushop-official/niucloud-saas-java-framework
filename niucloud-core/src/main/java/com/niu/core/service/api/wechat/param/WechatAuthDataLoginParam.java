package com.niu.core.service.api.wechat.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class WechatAuthDataLoginParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nickname = "";

    @NotNull(message = "openid参数缺失")
    @NotBlank(message = "openid不能为空")
    private String openid;

    private String unionId = "";

    private String headImgUrl = "";

    private Integer pid;
}
