package com.niu.cms.service.api.article.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.cms.entity.CmsArticle;
import com.niu.cms.mapper.CmsArticleMapper;
import com.niu.cms.service.admin.article.param.AllArticleSearchParam;
import com.niu.cms.service.api.article.IArticleService;
import com.niu.cms.service.api.article.param.ArticleSearchParam;
import com.niu.cms.service.api.article.vo.ArticleInfoVo;
import com.niu.cms.service.api.article.vo.ArticleListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.config.GlobalConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 文章实现
 */
@Service
public class ArticleImpl implements IArticleService {
        
    @Resource
    CmsArticleMapper cmsArticleMapper;

    /**
     * 文章列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult
     */
    @Override
    public PageResult<ArticleListVo> list(PageParam pageParam, ArticleSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();

        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<CmsArticle> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("ca").innerJoin("?_cms_article_category cac ON ca.category_id = cac.category_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("ca.*, cac.name as category_name");
        queryWrapper.eq("ca.site_id", siteId);
        queryWrapper.eq("ca.is_show", 1);
        queryWrapper.orderByDesc("id");
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) queryWrapper.like("title", searchParam.getTitle());
        if (searchParam.getCategoryId() > 0) queryWrapper.like("ca.category_id", searchParam.getCategoryId());

        IPage<ArticleListVo> iPage = cmsArticleMapper.selectJoinPage(new Page<>(page, limit), ArticleListVo.class, queryWrapper);
        List<ArticleListVo> list = new LinkedList<>();
        for (ArticleListVo item : iPage.getRecords()) {
            ArticleListVo vo = new ArticleListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit).setData(list);
    }

    /**
     * 文章详情
     * @param id 主键
     * @return MemberLabelInfoVo
     */
    @Override
    public ArticleInfoVo info(Integer id) {
        Integer siteId = RequestUtils.siteId();

        CmsArticle model = cmsArticleMapper.selectOne(
                new QueryWrapper<CmsArticle>()
                    .eq("site_id", siteId)
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "文章不存在");

        ArticleInfoVo vo = new ArticleInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 全部文章列表
     *
     * @param searchParam
     * @return
     */
    @Override
    public List<ArticleListVo> all(AllArticleSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();

        MPJQueryWrapper<CmsArticle> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("ca").innerJoin("?_cms_article_category cac ON ca.category_id = cac.category_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("ca.*, cac.name as category_name");
        queryWrapper.eq("ca.site_id", siteId);
        queryWrapper.eq("ca.is_show", 1);
        queryWrapper.orderByDesc("id");
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) queryWrapper.like("title", searchParam.getTitle());
        if (searchParam.getCategoryId() > 0) queryWrapper.like("ca.category_id", searchParam.getCategoryId());
        if (ObjectUtil.isNotEmpty(searchParam.getIds())) queryWrapper.in("id", searchParam.getIds());
        if (searchParam.getLimit() > 0) queryWrapper.last("limit " + searchParam.getLimit());

        List<ArticleListVo> list = cmsArticleMapper.selectJoinList(ArticleListVo.class, queryWrapper);
        return list;
    }
}
