package com.niu.cms.service.admin.article.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.cms.entity.CmsArticle;
import com.niu.cms.entity.CmsArticleCategory;
import com.niu.cms.mapper.CmsArticleCategoryMapper;
import com.niu.cms.mapper.CmsArticleMapper;
import com.niu.cms.service.admin.article.IArticleCategoryService;
import com.niu.cms.service.admin.article.param.CategoryParam;
import com.niu.cms.service.admin.article.param.CategorySearchParam;
import com.niu.cms.service.admin.article.vo.CategoryAllListVo;
import com.niu.cms.service.admin.article.vo.CategoryInfoVo;
import com.niu.cms.service.admin.article.vo.CategoryListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
            Long num = cmsArticleMapper.selectCount(new QueryWrapper<CmsArticle>().eq("category_id", item.getCategoryId()));
            vo.setArticleNum(num);
            list.add(vo);
        }
        return PageResult.build(page, limit,iPage.getTotal()).setData(list);
    }

    /**
     * 文章分类详情
     * @param id 主键
     * @return MemberLabelInfoVo
     */
    @Override
    public CategoryInfoVo info(Integer id) {
        Integer siteId = RequestUtils.siteId();

        CmsArticleCategory model = categoryMapper.selectOne(
                new QueryWrapper<CmsArticleCategory>()
                    .eq("site_id", siteId)
                    .eq("category_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "标签不存在");

        CategoryInfoVo vo = new CategoryInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 文章分类添加
     * @param addParam 参数
     */
    @Override
    public void add(CategoryParam addParam) {
        Integer siteId = RequestUtils.siteId();

        CmsArticleCategory model = new CmsArticleCategory();
        model.setSiteId(siteId);
        model.setName(addParam.getName());
        model.setIsShow(addParam.getIsShow());
        model.setSort(addParam.getSort());
        model.setCreateTime(System.currentTimeMillis() / 1000);

        categoryMapper.insert(model);
    }

    /**
     * 文章分类编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, CategoryParam editParam) {
        Integer siteId = RequestUtils.siteId();

        UpdateWrapper<CmsArticleCategory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("site_id", siteId)
                .eq("category_id",  id);

        CmsArticleCategory model = new CmsArticleCategory();
        model.setName(editParam.getName());
        model.setIsShow(editParam.getIsShow());
        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        categoryMapper.update(model, updateWrapper);
    }

    /**
     * 文章分类删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Integer siteId = RequestUtils.siteId();

        QueryWrapper<CmsArticleCategory> queryWrapper = new QueryWrapper<CmsArticleCategory>()
                .eq("site_id", siteId)
                .eq("category_id", id);

        categoryMapper.delete(queryWrapper);
    }

    /**
     * 查询全部文章分类
     * @return
     */
    @Override
    public List<CategoryAllListVo> all() {
        Integer siteId = RequestUtils.siteId();

        QueryWrapper<CmsArticleCategory> queryWrapper = new QueryWrapper<CmsArticleCategory>();
        queryWrapper.select("category_id,name").eq("site_id", siteId);

        List<CmsArticleCategory> labels = categoryMapper.selectList(queryWrapper); // 调用 selectList 方法

        List<CategoryAllListVo> list = new LinkedList<>();
        for (CmsArticleCategory item : labels) {
            CategoryAllListVo vo = new CategoryAllListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }
}
