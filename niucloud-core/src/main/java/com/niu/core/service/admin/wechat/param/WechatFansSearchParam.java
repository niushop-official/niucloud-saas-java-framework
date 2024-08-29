package com.niu.core.service.admin.wechat.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 微信粉丝列参数
 */
@Data
public class WechatFansSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String nickname;

    private String avatar;

    private Integer sex;

    private String language;

    private String country;

    private String province;

    private String city;

    private String district;

    private String openid;

    private String unionid;

    private Integer groupid;

    private Integer isSubscribe;

    private String remark;

    private Integer subscribeTime;

    private String subscribeScene;

    private Integer unsubscribeTime;

    private Integer appId;

}
