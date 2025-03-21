package com.niu.core.entity.weapp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信小程序版本实体
 */
@Data
public class WeappVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 粉丝ID */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    
    /**  站点id */
    private Integer siteId; 
    
    /**  版本 */
    private String version;
    
    /**  版本号 */
    private Integer versionNo;
    
    /**  说明 */
    @TableField(value = "`desc`")
    private String desc;

    /**  创建时间 */
    private Long createTime;

    /**  状态 */
    private Integer status;

    /**  更新时间 */
    private Long updateTime;

    /**  失败原因 */
    private String failReason;

    /**  上传任务key */
    private String taskKey;
    
    /**  提交来源 */
    private String fromType;
    
    /**  审核id */
    private String auditid;
    

}