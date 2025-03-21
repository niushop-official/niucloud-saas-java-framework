package com.niu.cms.controller.adminapi.article;

import com.niu.cms.service.admin.article.IArticleService;
import com.niu.cms.service.admin.article.param.ArticleParam;
import com.niu.cms.service.admin.article.param.ArticleSearchParam;
import com.niu.cms.service.admin.article.param.CategoryParam;
import com.niu.cms.service.admin.article.param.CategorySearchParam;
import com.niu.cms.service.admin.article.vo.ArticleInfoVo;
import com.niu.cms.service.admin.article.vo.ArticleListVo;
import com.niu.cms.service.admin.article.vo.CategoryInfoVo;
import com.niu.cms.service.admin.article.vo.CategoryListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("adminapi/cms/article")
public class ArticleController {

    @Resource
    IArticleService articleService;

    /**
     * 文章列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ArticleListVo>>
     */
    @GetMapping("")
    public Result<PageResult<ArticleListVo>> list(@Validated PageParam pageParam, @Validated ArticleSearchParam searchParam) {
        PageResult<ArticleListVo> list = articleService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 文章详情
     * @param id 主键ID
     * @return Result<ArticleInfoVo>
     */
    @GetMapping("/{id}")
    public Result<ArticleInfoVo> info(@PathVariable("id") Integer id) {
        ArticleInfoVo info = articleService.info(id);
        return Result.success(info);
    }

    /**
     * 文章添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody ArticleParam addParam) {
        articleService.add(addParam);
        return Result.success();
    }

    /**
     * 文章编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ArticleParam editParam) {
        articleService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 文章删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        articleService.del(id);
        return Result.success();
    }
}
