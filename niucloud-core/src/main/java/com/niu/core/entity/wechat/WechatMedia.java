package com.niu.core.entity.wechat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 微信素材实体
 */
@Data
public class WechatMedia implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    
    /** 站点id */
    private Integer siteId; 
    
    /** 类型 */
    private String type; 
    
    /** 值 */
    private String value; 
    
    /** 创建时间 */
    private Long createTime; 
    
    /** 修改时间 */
    private Long updateTime; 
    
    /** 微信端返回的素材id */
    private String mediaId; 

}