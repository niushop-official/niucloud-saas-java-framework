package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * SysArea列表视图
 */
@Data
public class SysAreaListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer pid;  // 父级
    private String name;  // 名称
    private String shortname;  // 简称
    private String longitude;  // 经度
    private String latitude;  // 纬度
    private Integer level;  // 级别
    private Integer sort;  // 排序
    private Integer status;  // 状态1有效

}
