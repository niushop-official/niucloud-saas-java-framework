package com.niu.cms.service.admin.article;

import com.niu.cms.service.admin.article.param.CategoryParam;
import com.niu.cms.service.admin.article.param.CategorySearchParam;
import com.niu.cms.service.admin.article.vo.CategoryAllListVo;
import com.niu.cms.service.admin.article.vo.CategoryInfoVo;
import com.niu.cms.service.admin.article.vo.CategoryListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.param.MemberLabelEditParam;
import com.niu.core.service.admin.member.param.MemberLabelSearchParam;
import com.niu.core.service.admin.member.vo.MemberLabelAllListVo;
import com.niu.core.service.admin.member.vo.MemberLabelInfoVo;
import com.niu.core.service.admin.member.vo.MemberLabelListVo;

import java.util.List;

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

    /**
     * 文章分类详情
     * @param id 主键ID
     * @return MemberLabelInfoVo
     */
    CategoryInfoVo info(Integer id);

    /**
     * 文章分类添加
     * @param addParam 添加参数
     */
    void add(CategoryParam addParam);

    /**
     * 文章分类编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, CategoryParam editParam);

    /**
     * 文章分类删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 全部文章分类
     * @return
     */
    List<CategoryAllListVo> all();
}
