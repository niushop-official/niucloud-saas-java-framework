package com.niu.core.entity.wechat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 公众号消息回调实体
 */
@Data
public class WechatReply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;  
    
    /** 规则名称 */
    private String name; 
    
    /** 站点id */
    private Integer siteId; 
    
    /** 关键词 */
    private String keyword; 
    
    /** 回复类型 subscribe-关注回复 keyword-关键字回复 default-默认回复 */
    private String replyType; 
    
    /** 匹配方式：full 全匹配；like-模糊匹配 */
    private String matchingType; 
    
    /** 回复内容 */
    private String content; 
    
    /** 排序 */
    private Integer sort; 
    
    /** 创建时间 */
    private Long createTime; 
    
    /** 更新时间 */
    private Long updateTime; 
    
    /** 删除时间 */
    private Long deleteTime; 
    
    /** 回复方式 all 全部 rand随机 */
    private String replyMethod; 

}