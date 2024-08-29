package com.niu.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CmsArticleCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="category_id", type= IdType.AUTO)
    private Integer categoryId;

    /** 站点id */
    private Integer siteId;

    /** 分类名称 */
    private String name;

    /** 排序 */
    private Integer sort;

    /** 是否显示 */
    private Integer isShow;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;


}
