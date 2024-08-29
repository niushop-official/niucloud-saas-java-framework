package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 海报实体
 */
@Data
public class SysPoster implements Serializable {

    
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    
    /** 站点id */
    private Integer siteId;
    
    /** 海报名称 */
    private String name;
    
    /** 海报类型 */
    private String type;
    
    /** 海报支持渠道 */
    private String channel;
    
    /** 配置值json */
    private String value;
    
    /** 是否启用 1启用 2不启用 */
    private Integer status;
    
    /** 所属插件 */
    private String addon;
    
    /** 是否默认海报，1：是，0：否 */
    private Integer isDefault;
    
    /** 创建时间 */
    private Long createTime;
    
    /** 修改时间 */
    private Long updateTime;

}