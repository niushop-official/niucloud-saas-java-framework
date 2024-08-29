package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * SysPoster列表视图
 */
@Data
public class SysPosterListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 主键
    private Integer siteId;  // 站点id
    private String name;  // 海报名称
    private String type;  // 海报类型
    private String channel;  // 海报支持渠道
    private String value;  // 配置值json
    private Integer status;  // 是否启用 1启用 2不启用
    private String addon;  // 所属插件
    private Integer isDefault;  // 是否默认海报，1：是，0：否
    private String createTime; // 创建时间
    private String updateTime; // 修改时间

}
