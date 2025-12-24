package com.niu.cms.service.api.article.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.cms.entity.CmsArticle;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 */
@Data
public class ArticleInfoVo extends CmsArticle implements Serializable {
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
}
