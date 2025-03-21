package com.niu.shop.controller.adminapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.goods.ILabelGroupService;
import com.niu.shop.service.admin.goods.param.LabelGroupModifySortParam;
import com.niu.shop.service.admin.goods.param.LabelGroupParam;
import com.niu.shop.service.admin.goods.param.LabelGroupSearchParam;
import com.niu.shop.service.admin.goods.vo.LabelGroupInfoVo;
import com.niu.shop.service.admin.goods.vo.LabelGroupListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品标签分组表控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods/label/group")
public class LabelGroupController {

    @Resource
    ILabelGroupService labelGroupService;

    /**
     * 商品标签分组表分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<LabelGroupListVo>>
     */
    @GetMapping("")
    public Result<PageResult<LabelGroupListVo>> page(@Validated PageParam pageParam, @Validated LabelGroupSearchParam searchParam) {
        PageResult<LabelGroupListVo> list = labelGroupService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品标签分组列表
     * @param searchParam
     * @return
     */
    @GetMapping("/list")
    public Result<List<LabelGroupListVo>> list(@Validated LabelGroupSearchParam searchParam) {
        return Result.success(labelGroupService.list(searchParam));
    }

    /**
     * 商品标签分组表详情
     *
     * @param id 主键ID
     * @return Result<LabelGroupInfoVo>
     */
    @GetMapping("/{id}")
    public Result<LabelGroupInfoVo> info(@PathVariable("id") Integer id) {
        LabelGroupInfoVo info = labelGroupService.info(id);
        return Result.success(info);
    }

    /**
     * 商品标签分组表添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody LabelGroupParam addParam) {
        labelGroupService.add(addParam);
        return Result.success();
    }

    /**
     * 商品标签分组表编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody LabelGroupParam editParam) {
        labelGroupService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品标签分组表删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        labelGroupService.del(id);
        return Result.success();
    }

    /**
     * 修改商品标签分组排序号
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/sort")
    public Result<Object> modifySort(@Validated @RequestBody LabelGroupModifySortParam editParam) {
        labelGroupService.modifySort(editParam);
        return Result.success();
    }
}
