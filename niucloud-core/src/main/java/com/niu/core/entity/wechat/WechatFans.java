package com.niu.core.entity.wechat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 微信粉丝列实体
 */
@Data
public class WechatFans implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 粉丝ID */
    @TableId(value="fans_id", type= IdType.AUTO)
    private Integer fansId;
    
    /**  站点id */
    private Integer siteId; 
    
    /**  昵称 */
    private String nickname; 
    
    /**  头像 */
    private String avatar; 
    
    /**  性别 */
    private Integer sex; 
    
    /**  用户语言 */
    private String language; 
    
    /**  国家 */
    private String country; 
    
    /**  省 */
    private String province; 
    
    /**  城市 */
    private String city; 
    
    /**  行政区/县 */
    private String district; 
    
    /**  用户的标识，对当前公众号唯一     用户的唯一身份ID */
    private String openid; 
    
    /**  粉丝unionid */
    private String unionid; 
    
    /**  粉丝所在组id */
    private Integer groupid; 
    
    /**  是否订阅 */
    private Integer isSubscribe; 
    
    /**  备注 */
    private String remark; 
    
    /**  关注时间 */
    private Integer subscribeTime; 
    
    /**  返回用户关注的渠道来源 */
    private String subscribeScene; 
    
    /**  取消关注时间 */
    private Integer unsubscribeTime; 
    
    /**  粉丝信息最后更新时间 */
    private Long updateTime; 
    
    /**  应用appid */
    private Integer appId; 

}