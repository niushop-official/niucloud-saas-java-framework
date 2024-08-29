package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 地址实体
 */
@Data
public class SysArea implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 父级 */
    private Integer pid;

    /** 名称 */
    private String name;

    /** 简称 */
    private String shortname;

    /** 经度 */
    private String longitude;

    /** 纬度 */
    private String latitude;

    /** 级别 */
    private Integer level;

    /** 排序 */
    private Integer sort;

    /** 状态1有效 */
    private Integer status;

}