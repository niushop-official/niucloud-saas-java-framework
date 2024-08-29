package com.niu.core.entity.applet;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 小程序版本实体
 */
@Data
public class AppletVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 配置信息 */
    private String config;

    /** 小程序类型 */
    private String type;

    /** 插件描述 */
    private String desc;

    /** 状态  下架  上架 */
    private Integer status;

    /** 发布者 */
    private String uid;

    /** 小程序包地址 */
    private String path;

    /** 版本号 */
    private String version;

    /** 版本号数字(用于排序) */
    private String versionNum;

    /** 发布线上版本号 */
    private String releaseVersion;

    /** 创建时间 */
    private Long createTime;

    /** 删除时间 */
    private Long deleteTime;

    /** 更新时间 */
    private Long updateTime;


}