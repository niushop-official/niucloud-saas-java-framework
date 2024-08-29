package com.niu.cms.service.admin.article.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.cms.entity.CmsArticle;
import com.niu.cms.entity.CmsArticleCategory;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class ArticleListVo extends CmsArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 添加时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime; // 更新时间

    private ArticleUrl articleUrl;

    private String categoryName;

    @Data
    public static class ArticleUrl {
        private String wapUrl;

        private String webUrl;
    }
}
