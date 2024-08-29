package com.niu.cms.service.api.article;

import com.niu.cms.service.admin.article.param.AllArticleSearchParam;
import com.niu.cms.service.api.article.param.ArticleSearchParam;
import com.niu.cms.service.api.article.vo.ArticleInfoVo;
import com.niu.cms.service.api.article.vo.ArticleListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;

import java.util.List;

/**
 * 文章服务接口
 */
public interface IArticleService {


    /**
     * 文章列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult
     */
    PageResult<ArticleListVo> list(PageParam pageParam, ArticleSearchParam searchParam);

    /**
     * 文章详情
     * @param id 主键ID
     * @return MemberLabelInfoVo
     */
    ArticleInfoVo info(Integer id);

    /**
     * 全部文章列表
     *
     * @param searchParam
     * @return
     */
    List<ArticleListVo> all(AllArticleSearchParam searchParam);
}
