package com.niu.cms.service.admin.article.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.cms.entity.CmsArticle;
import com.niu.cms.mapper.CmsArticleMapper;
import com.niu.cms.service.admin.article.IArticleService;
import com.niu.cms.service.admin.article.param.ArticleParam;
import com.niu.cms.service.admin.article.param.ArticleSearchParam;
import com.niu.cms.service.admin.article.vo.*;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 文章实现
 */
@Service
public class ArticleImpl implements IArticleService {
        
    @Resource
    CmsArticleMapper cmsArticleMapper;

    @Resource
    ICoreSysConfigService coreSysConfigService;

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
        queryWrapper.orderByDesc("id");
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) queryWrapper.like("title", searchParam.getTitle());
        if (searchParam.getCategoryId() > 0) queryWrapper.like("ca.category_id", searchParam.getCategoryId());

        SceneDomainVo sceneDomain = coreSysConfigService.getSceneDomain(siteId);

        IPage<ArticleListVo> iPage = cmsArticleMapper.selectJoinPage(new Page<>(page, limit), ArticleListVo.class, queryWrapper);
        List<ArticleListVo> list = new LinkedList<>();
        for (ArticleListVo item : iPage.getRecords()) {
            ArticleListVo vo = new ArticleListVo();
            BeanUtils.copyProperties(item, vo);
            ArticleListVo.ArticleUrl articleUrl = new ArticleListVo.ArticleUrl();
            articleUrl.setWapUrl(sceneDomain.getWapUrl() + "/addon/cms/pages/detail?id=" + item.getId());
            articleUrl.setWebUrl(sceneDomain.getWebUrl() + "/article/detail?id=" + item.getId());
            vo.setArticleUrl(articleUrl);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
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
     * 文章添加
     * @param addParam 参数
     */
    @Override
    public void add(ArticleParam addParam) {
        CmsArticle model = new CmsArticle();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        cmsArticleMapper.insert(model);
    }

    /**
     * 文章编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ArticleParam editParam) {
        Integer siteId = RequestUtils.siteId();

        UpdateWrapper<CmsArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("site_id", siteId)
                .eq("id",  id);

        CmsArticle model = new CmsArticle();
        BeanUtils.copyProperties(editParam, model);
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        cmsArticleMapper.update(model, updateWrapper);
    }

    /**
     * 文章删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Integer siteId = RequestUtils.siteId();

        QueryWrapper<CmsArticle> queryWrapper = new QueryWrapper<CmsArticle>()
                .eq("site_id", siteId)
                .eq("id", id);

        cmsArticleMapper.delete(queryWrapper);
    }
}
