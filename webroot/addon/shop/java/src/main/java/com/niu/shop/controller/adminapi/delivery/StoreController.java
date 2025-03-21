package com.niu.shop.controller.adminapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.delivery.param.ShopStoreParam;
import com.niu.shop.service.admin.delivery.param.ShopStoreSearchParam;
import com.niu.shop.service.admin.delivery.IShopStoreService;
import com.niu.shop.service.admin.delivery.vo.ShopStoreInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopStoreListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 自提门店控制器
 */

@RestController
@RequestMapping("adminapi/shop/delivery/store")
public class StoreController {

    @Resource
    IShopStoreService storeService;

    /**
     * 自提门店分页列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopStoreListVo>>
     */
    @GetMapping("")
    public Result<PageResult<ShopStoreListVo>> page(@Validated PageParam pageParam, @Validated ShopStoreSearchParam searchParam) {
        PageResult<ShopStoreListVo> list = storeService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 自提门店列表
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopStoreListVo>>
     */
    @GetMapping("/list")
    public Result<List<ShopStoreListVo>> page(@Validated ShopStoreSearchParam searchParam) {
        List<ShopStoreListVo> list = storeService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 自提门店详情
     * @param id 主键ID
     * @return Result<ShopStoreInfoVo>
     */
    @GetMapping("/{id}")
    public Result<ShopStoreInfoVo> info(@PathVariable("id") Integer id) {
        ShopStoreInfoVo info = storeService.info(id);
        return Result.success(info);
    }

    /**
     * 自提门店添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody ShopStoreParam addParam) {
        storeService.add(addParam);
        return Result.success();
    }

    /**
     * 自提门店编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopStoreParam editParam) {
        storeService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 自提门店删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        storeService.del(id);
        return Result.success();
    }

}
