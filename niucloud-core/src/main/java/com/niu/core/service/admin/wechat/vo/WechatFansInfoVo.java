package com.niu.core.service.admin.wechat.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * WechatFans视图
 */
@Data
public class WechatFansInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer fansId;  // 粉丝ID
    private Integer siteId;  // 站点id
    private String nickname;  // 昵称
    private String avatar;  // 头像
    private Integer sex;  // 性别
    private String language;  // 用户语言
    private String country;  // 国家
    private String province;  // 省
    private String city;  // 城市
    private String district;  // 行政区/县
    private String openid;  // 用户的标识，对当前公众号唯一     用户的唯一身份ID
    private String unionid;  // 粉丝unionid
    private Integer groupid;  // 粉丝所在组id
    private Integer isSubscribe;  // 是否订阅
    private String remark;  // 备注
    private Integer subscribeTime;  // 关注时间
    private String subscribeScene;  // 返回用户关注的渠道来源
    private Integer unsubscribeTime;  // 取消关注时间
    private Integer appId;  // 应用appid

}
