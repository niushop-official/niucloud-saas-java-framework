package com.niu.core.entity.diy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 自定义页面实体
 */
@Data
public class DiyPage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 页面名称（用于后台展示） */
    private String pageTitle;

    /** 页面标题（用于前台展示） */
    private String title;

    /** 页面标识 */
    private String name;

    /** 页面模板 */
    private String type;

    /** 模板名称 */
    private String template;

    /** 页面展示模式，diy：自定义，fixed：固定 */
    private String mode;

    /** 页面数据，json格式 */
    private String value;

    /** 是否默认页面，1：是，0：否 */
    private Integer isDefault;

    /** 数据是否发生过变化，1：变化了，2：没有 */
    private Integer isChange;

    /** 分享内容 */
    private String share;

    /** 访问量 */
    private Integer visitCount;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;

}