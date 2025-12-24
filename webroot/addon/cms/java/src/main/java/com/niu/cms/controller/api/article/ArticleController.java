package com.niu.cms.controller.api.article;

import com.niu.cms.service.admin.article.param.AllArticleSearchParam;
import com.niu.cms.service.api.article.IArticleService;
import com.niu.cms.service.api.article.param.ArticleSearchParam;
import com.niu.cms.service.api.article.vo.ArticleInfoVo;
import com.niu.cms.service.api.article.vo.ArticleListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/cms/article")
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
     * 全部文章
     * @param searchParam
     * @return
     */
    @GetMapping("/all")
    public Result<List<ArticleListVo>> all(AllArticleSearchParam searchParam) {
        return Result.success(articleService.all(searchParam));
    }

    /**
     * 自增文章浏览量
     * @param id 文章ID
     * @return Result<Object>
     */
    @PutMapping("/visit/{id}")
    public Result<?> visit(@PathVariable("id") Integer id) {
        articleService.incrVisit(id);
        return Result.success("SUCCESS");
    }
}
