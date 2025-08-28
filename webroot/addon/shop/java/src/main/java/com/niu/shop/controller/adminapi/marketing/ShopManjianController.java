package com.niu.shop.controller.adminapi.marketing;

import jdk.jfr.Description;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.enums.marketing.ShopManjianStatusEnum;
import com.niu.shop.service.admin.marketing.IShopManjianService;
import com.niu.shop.service.admin.marketing.param.ShopManjianBatchParam;
import com.niu.shop.service.admin.marketing.param.ShopManjianCheckGoodsParam;
import com.niu.shop.service.admin.marketing.param.ShopManjianGoodsCheckParam;
import com.niu.shop.service.admin.marketing.param.ShopManjianInitParam;
import com.niu.shop.service.admin.marketing.vo.ShopManjianInitVo;
import com.niu.shop.service.admin.marketing.vo.ShopManjianMemberVo;
import com.niu.shop.service.core.marketing.param.ShopManjianParam;
import com.niu.shop.service.core.marketing.param.ShopManjianSearchParam;
import com.niu.shop.service.core.marketing.vo.ShopManjianInfoVo;
import com.niu.shop.service.core.marketing.vo.ShopManjianListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 满减活动表控制器
 */

@RestController
@RequestMapping("adminapi/shop")
@Description("满减活动管理")
public class ShopManjianController {

    @Resource
    IShopManjianService manjianService;

    /**
     * 获取满减列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/manjian")
    @Description("获取满减活动分页列表")
    public Result<PageResult<ShopManjianListVo>> lists(@Validated PageParam pageParam,
                                                        @Validated ShopManjianSearchParam searchParam) {
        PageResult<ShopManjianListVo> list = manjianService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 满减送关闭
     *
     * @param id 满减活动ID
     * @return ResponseEntity 返回表示操作成功的响应实体
     */
    @PutMapping("/manjian/close/{id}")
    @Description("关闭满减活动")
    public Result<Object> closeManjian(@Validated @PathVariable("id") Integer id) {
        manjianService.closeManjian(id);
        return Result.success();
    }

    /**
     * 满减送删除
     *
     * @param id 满减活动ID
     */
    @DeleteMapping("/manjian/{id}")
    @Description("删除满减活动")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        manjianService.del(id);
        return Result.success();
    }

    /**
     * 满减送详情
     * @param id
     * @return
     */
    @GetMapping("/manjian/{id}")
    @Description("获取满减活动详情")
    public Result<ShopManjianInfoVo> info(@Validated @PathVariable("id") Integer id) {
        return Result.success(manjianService.info(id));
    }

    /**
     * 获取满减会员
     * @param id
     * @param pageParam
     * @return
     */
    @GetMapping("/manjian/member/{id}")
    @Description("获取满减活动会员列表")
    public Result<PageResult<ShopManjianMemberVo>> member(@Validated @PathVariable("id") Integer id, @Validated PageParam pageParam) {
        return Result.success(manjianService.member(pageParam, id));
    }

    /**
     * 添加满减送
     * @param data
     * @return
     */
    @PostMapping("/manjian")
    @Description("添加满减活动")
    public Result<Object> add(@Validated @RequestBody ShopManjianParam data) {
        return Result.success(manjianService.add(data));
    }

    /**
     * 获取满减初始化数据
     * @param data
     * @return
     */
    @GetMapping("/manjian/init")
    @Description("获取满减活动初始化数据")
    public Result<ShopManjianInitVo> init(@Validated ShopManjianInitParam data) {
        return Result.success(manjianService.getInit(data));
    }

    /**
     * 编辑满减
     * @param id
     * @param data
     * @return
     */
    @PutMapping("/manjian/{id}")
    @Description("编辑满减活动")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody ShopManjianParam data) {

        return Result.success(manjianService.edit(id, data));
    }

    /**
     * 获取活动状态
     *
     * @return ResponseEntity 返回包含活动状态信息的响应实体（这里假设`ManjianDict`枚举类有对应的获取状态的方法来获取状态数据）
     */
    @GetMapping("/manjian/status")
    @Description("获取满减活动状态")
    public Result<Object> status() {
        return Result.success(ShopManjianStatusEnum.getMap());
    }

    /**
     * 检测商品
     * @param data
     * @return
     */
    @PostMapping("/manjian/goods/check")
    @Description("检测满减活动商品")
    public Result<Object> checkGoods(@Validated @RequestBody ShopManjianCheckGoodsParam data) {
        manjianService.checkGoods(data);
        return Result.success();
    }

    /**
     * 批量关闭
     * @param data
     * @return
     */
    @PutMapping("/manjian/goods/batchClose")
    @Description("批量关闭满减活动")
    public Result<Object> batchClose(@Validated @RequestBody ShopManjianBatchParam data) {
        manjianService.batchClose(data);
        return Result.success();
    }

    /**
     * 批量删除
     * @param data
     * @return
     */
    @DeleteMapping("/manjian/goods/batchDelete")
    @Description("批量删除满减活动")
    public Result<Object> batchDelete(@Validated @RequestBody ShopManjianBatchParam data) {
        manjianService.batchDel(data);
        return Result.success();
    }
}
