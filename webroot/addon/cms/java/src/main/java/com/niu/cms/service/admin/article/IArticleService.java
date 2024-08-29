package com.niu.cms.service.admin.article;

import com.niu.cms.service.admin.article.param.ArticleParam;
import com.niu.cms.service.admin.article.param.ArticleSearchParam;
import com.niu.cms.service.admin.article.vo.*;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;

import java.util.List;

/**
 * 文章服务接口
 */
public interface IArticleService {


    /**
     * 文章分类列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult
     */
    PageResult<ArticleListVo> list(PageParam pageParam, ArticleSearchParam searchParam);

    /**
     * 文章分类详情
     * @param id 主键ID
     * @return MemberLabelInfoVo
     */
    ArticleInfoVo info(Integer id);

    /**
     * 文章分类添加
     * @param addParam 添加参数
     */
    void add(ArticleParam addParam);

    /**
     * 文章分类编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ArticleParam editParam);

    /**
     * 文章分类删除
     * @param id 主键ID
     */
    void del(Integer id);
}
