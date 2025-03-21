package com.niu.cms.controller.adminapi.article;

import com.niu.cms.service.admin.article.IArticleCategoryService;
import com.niu.cms.service.admin.article.param.CategoryParam;
import com.niu.cms.service.admin.article.param.CategorySearchParam;
import com.niu.cms.service.admin.article.vo.CategoryAllListVo;
import com.niu.cms.service.admin.article.vo.CategoryInfoVo;
import com.niu.cms.service.admin.article.vo.CategoryListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.member.param.MemberLabelEditParam;
import com.niu.core.service.admin.member.param.MemberLabelSearchParam;
import com.niu.core.service.admin.member.vo.MemberLabelAllListVo;
import com.niu.core.service.admin.member.vo.MemberLabelInfoVo;
import com.niu.core.service.admin.member.vo.MemberLabelListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("adminapi/cms/category")
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

    /**
     * 文章分类详情
     * @param id 主键ID
     * @return Result<CategoryInfoVo>
     */
    @GetMapping("/{id}")
    public Result<CategoryInfoVo> info(@PathVariable("id") Integer id) {
        CategoryInfoVo info = articleCategoryService.info(id);
        return Result.success(info);
    }

    /**
     * 文章分类添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody CategoryParam addParam) {
        articleCategoryService.add(addParam);
        return Result.success();
    }

    /**
     * 文章分类编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody CategoryParam editParam) {
        articleCategoryService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 文章分类删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        articleCategoryService.del(id);
        return Result.success();
    }

    /**
     * 全部文章分类
     * @return
     */
    @GetMapping("/all")
    public Result<List<CategoryAllListVo>> all() {
        List<CategoryAllListVo> list = articleCategoryService.all();
        return Result.success(list);
    }
}
