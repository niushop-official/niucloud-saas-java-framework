package com.niu.core.service.admin.wechat.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 微信粉丝列参数
*/
@Data
public class WechatFansParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "fansId参数缺失")
    private Integer fansId;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "nickname参数缺失")
    private String nickname;

    @NotNull(message = "avatar参数缺失")
    private String avatar;

    @NotNull(message = "sex参数缺失")
    private Integer sex;

    @NotNull(message = "language参数缺失")
    private String language;

    @NotNull(message = "country参数缺失")
    private String country;

    @NotNull(message = "province参数缺失")
    private String province;

    @NotNull(message = "city参数缺失")
    private String city;

    @NotNull(message = "district参数缺失")
    private String district;

    @NotNull(message = "openid参数缺失")
    private String openid;

    @NotNull(message = "unionid参数缺失")
    private String unionid;

    @NotNull(message = "groupid参数缺失")
    private Integer groupid;

    @NotNull(message = "isSubscribe参数缺失")
    private Integer isSubscribe;

    @NotNull(message = "remark参数缺失")
    private String remark;

    @NotNull(message = "subscribeTime参数缺失")
    private Integer subscribeTime;

    @NotNull(message = "subscribeScene参数缺失")
    private String subscribeScene;

    @NotNull(message = "unsubscribeTime参数缺失")
    private Integer unsubscribeTime;

    @NotNull(message = "appId参数缺失")
    private Integer appId;

}
