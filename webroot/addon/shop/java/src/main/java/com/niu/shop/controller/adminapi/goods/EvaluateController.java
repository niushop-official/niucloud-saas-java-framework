package com.niu.shop.controller.adminapi.goods;

import cn.hutool.json.JSONUtil;
import com.niu.shop.enums.goods.GoodsEvaluateEnum;
import com.niu.shop.service.admin.goods.param.ShopGoodsEvaluateBatchParam;
import jdk.jfr.Description;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品评价控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods/evaluate")
@Description("商品评价管理")
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
    @Description("获取商品评价分页列表")
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
    @Description("获取商品评价详情")
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
    @Description("添加商品评价")
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
    @Description("删除商品评价")
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
    @Description("审核通过商品评价")
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
    @Description("拒绝商品评价")
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
    @Description("回复商品评价")
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
    @Description("置顶商品评价")
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
    @Description("取消置顶商品评价")
    public Result<Object> cancelTopping(@PathVariable("id") Integer id) {
        shopGoodsEvaluateService.cancelTopping(id);
        return Result.success();
    }

    /**
     * 批量通过
     * @return
     */
    @PostMapping("batch/adopt")
    @Description("批量通过")
    public Result batchAdopt(@RequestBody ShopGoodsEvaluateBatchParam shopGoodsEvaluateBatchParam) {
        shopGoodsEvaluateService.batchAdopt(shopGoodsEvaluateBatchParam);
        return Result.success();
    }

    /**
     * 批量拒绝
     * @return
     */
    @PostMapping("batch/refuse")
    @Description("批量拒绝")
    public Result batchRefuse(@RequestBody ShopGoodsEvaluateBatchParam shopGoodsEvaluateBatchParam) {
        shopGoodsEvaluateService.batchRefuse(shopGoodsEvaluateBatchParam);
        return Result.success();
    }

    /**
     * 批量删除
     * @return
     */
    @PostMapping("batch/del")
    @Description("批量删除")
    public Result batchDel(@RequestBody ShopGoodsEvaluateBatchParam shopGoodsEvaluateBatchParam) {
        shopGoodsEvaluateService.batchDel(shopGoodsEvaluateBatchParam);
        return Result.success();
    }

    @GetMapping("status")
    @Description("获取审核状态")
    public Result getStatus() {
        Map map = GoodsEvaluateEnum.getMap();
        List<String> list = new ArrayList<>();
        for (Object key : map.keySet()) {
            list.add(JSONUtil.parseObj(map.get(key)).get("name").toString());
        }
        return Result.success(list);
    }
}
