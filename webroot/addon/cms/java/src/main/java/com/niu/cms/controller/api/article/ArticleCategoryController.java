package com.niu.cms.controller.api.article;

import com.niu.cms.service.api.article.IArticleCategoryService;
import com.niu.cms.service.api.article.param.CategorySearchParam;
import com.niu.cms.service.api.article.vo.CategoryListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("api/cms/category")
public class ArticleCategoryController {
    
    @Resource
    IArticleCategoryService articleCategoryService;

    /**
     * 文章分类列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<CategoryListVo>>
     */
    @GetMapping("")
    public Result<PageResult<CategoryListVo>> list(@Validated PageParam pageParam, @Validated CategorySearchParam searchParam) {
        PageResult<CategoryListVo> list = articleCategoryService.list(pageParam, searchParam);
        return Result.success(list);
    }
}
