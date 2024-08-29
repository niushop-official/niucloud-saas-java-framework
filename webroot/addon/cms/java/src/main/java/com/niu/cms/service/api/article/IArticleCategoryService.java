package com.niu.cms.service.api.article;

import com.niu.cms.service.api.article.param.CategorySearchParam;
import com.niu.cms.service.api.article.vo.CategoryListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;

/**
 * 文章分类服务接口
 */
public interface IArticleCategoryService {


    /**
     * 文章分类列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult
     */
    PageResult<CategoryListVo> list(PageParam pageParam, CategorySearchParam searchParam);

}
