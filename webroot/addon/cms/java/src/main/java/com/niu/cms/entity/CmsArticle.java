package com.niu.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CmsArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 文章分类 */
    private Integer categoryId;

    /** 文章标题 */
    private String title;

    /** 简介 */
    private String intro;

    /** 文章摘要 */
    private String summary;

    /** 文章图片 */
    private String image;

    /** 作者 */
    private String author;

    /** 文章内容 */
    private String content;

    /** 实际浏览量 */
    private Integer visit;

    /** 初始浏览量 */
    private Long visitVirtual;

    /** 排序 */
    private Long sort;

    /** 是否显示 */
    private Integer isShow;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;

}
