package com.niu.cms.service.admin.article.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员标签参数
 */
@Data
public class CategorySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

}
