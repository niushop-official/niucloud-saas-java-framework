package com.niu.cms.service.admin.article.vo;

import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class CategoryAllListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer categoryId;

    /** 分类名称 */
    private String name;

}
