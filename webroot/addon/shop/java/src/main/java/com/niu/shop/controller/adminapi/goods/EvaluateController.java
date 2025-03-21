package com.niu.shop.controller.adminapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.ShopGoodsEvaluateParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsEvaluateReplyParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsEvaluateSearchParam;
import com.niu.shop.service.admin.goods.IShopGoodsEvaluateService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsEvaluateInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsEvaluateListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 商品评价控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods/evaluate")
public class EvaluateController {

    @Resource
    IShopGoodsEvaluateService shopGoodsEvaluateService;

    /**
     * 商品评价列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsEvaluateListVo>>
     */
    @GetMapping("")
    public Result<PageResult<ShopGoodsEvaluateListVo>> list(@Validated PageParam pageParam,
                                                     @Validated ShopGoodsEvaluateSearchParam searchParam) {
        PageResult<ShopGoodsEvaluateListVo> list = shopGoodsEvaluateService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品评价详情
     * @param id 主键ID
     * @return Result<ShopGoodsEvaluateInfoVo>
     */
    @GetMapping("/{id}")
    public Result<ShopGoodsEvaluateInfoVo> info(@Validated @PathVariable("id") Integer id) {
        ShopGoodsEvaluateInfoVo info = shopGoodsEvaluateService.info(id);
        return Result.success(info);
    }

    /**
     * 商品评价添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody ShopGoodsEvaluateParam addParam) {
        shopGoodsEvaluateService.add(addParam);
        return Result.success();
    }


    /**
     * 商品评价删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        shopGoodsEvaluateService.del(id);
        return Result.success();
    }

    /**
     * 审核通过
     * @param id
     * @return
     */
    @PutMapping("adopt/{id}")
    public Result<Object> adopt(@PathVariable("id") Integer id) {
        shopGoodsEvaluateService.auditAdopt(id);
        return Result.success();
    }

    /**
     * 审核拒绝
     * @param id
     * @return
     */
    @PutMapping("refuse/{id}")
    public Result<Object> refuse(@PathVariable("id") Integer id) {
        shopGoodsEvaluateService.auditRefuse(id);
        return Result.success();
    }

    /**
     * 评价回复
     * @param id
     * @param replyParam
     * @return
     */
    @PutMapping("reply/{id}")
    public Result<Object> evaluateReply(@PathVariable("id") Integer id, @RequestBody ShopGoodsEvaluateReplyParam replyParam){
        shopGoodsEvaluateService.reply(id, replyParam.getExplainFirst());
        return Result.success();
    }

    /**
     * 审核置顶
     * @param id
     * @return
     */
    @PutMapping("topping/{id}")
    public Result<Object> topping(@PathVariable("id") Integer id) {
        shopGoodsEvaluateService.topping(id);
        return Result.success();
    }

    /**
     * 审核置顶取消
     * @param id
     * @return
     */
    @PutMapping("cancel_topping/{id}")
    public Result<Object> cancelTopping(@PathVariable("id") Integer id) {
        shopGoodsEvaluateService.cancelTopping(id);
        return Result.success();
    }


}
