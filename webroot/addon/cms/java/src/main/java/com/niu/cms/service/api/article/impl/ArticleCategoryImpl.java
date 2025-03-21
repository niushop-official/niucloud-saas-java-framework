package com.niu.cms.service.api.article.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.cms.entity.CmsArticleCategory;
import com.niu.cms.mapper.CmsArticleCategoryMapper;
import com.niu.cms.mapper.CmsArticleMapper;
import com.niu.cms.service.api.article.IArticleCategoryService;
import com.niu.cms.service.api.article.param.CategorySearchParam;
import com.niu.cms.service.api.article.vo.CategoryListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 文章分类实现
 */
@Service
public class ArticleCategoryImpl implements IArticleCategoryService {
        
    @Resource
    CmsArticleCategoryMapper categoryMapper;

    @Resource
    CmsArticleMapper cmsArticleMapper;

    /**
     * 文章分类列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult
     */
    @Override
    public PageResult<CategoryListVo> list(PageParam pageParam, CategorySearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();

        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<CmsArticleCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.orderByDesc("category_id");
        if (ObjectUtil.isNotEmpty(searchParam.getName())) queryWrapper.like("name", searchParam.getName());

        IPage<CmsArticleCategory> iPage = categoryMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<CategoryListVo> list = new LinkedList<>();
        for (CmsArticleCategory item : iPage.getRecords()) {
            CategoryListVo vo = new CategoryListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

}
