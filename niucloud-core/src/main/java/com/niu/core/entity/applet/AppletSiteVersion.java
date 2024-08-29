package com.niu.core.entity.applet;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 站点小程序版本实体
 */
@Data
public class AppletSiteVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 版本id */
    private Integer versionId;

    /** 小程序类型 */
    private String type;

    /** 操作方式 download 下载  upgrade 更新 */
    private String action;

    /** 创建时间 */
    private Long createTime;

}