package com.niu.core.entity.diy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 自定义路由实体
 */
@Data
public class DiyRoute implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 页面名称 */
    private String title;

    /** 页面标识 */
    private String name;

    /** 页面路径 */
    private String page;

    /** 分享内容 */
    private String share;

    /** 是否支持分享 */
    private Integer isShare;

    /** 排序 */
    private Integer sort;

}