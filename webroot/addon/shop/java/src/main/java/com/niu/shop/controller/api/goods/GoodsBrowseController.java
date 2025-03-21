package com.niu.shop.controller.api.goods;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.goods.IGoodsBrowseService;
import com.niu.shop.service.api.goods.param.GoodsBrowseDeleteParam;
import com.niu.shop.service.api.goods.param.GoodsBrowseParam;
import com.niu.shop.service.api.goods.param.GoodsBrowseSearchParam;
import com.niu.shop.service.api.goods.vo.GoodsBrowseListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/shop/goods/browse")
public class GoodsBrowseController {

    @Resource
    IGoodsBrowseService browseService;

    /**
     * 商品足迹
     *
     * @param searchParam
     * @return
     */
    @GetMapping("")
    public Result<PageResult<GoodsBrowseListVo>> list(PageParam pageParam, GoodsBrowseSearchParam searchParam) {
        return Result.success(browseService.page(pageParam, searchParam));
    }

    /**
     * 商品足迹添加
     *
     * @param addParam
     * @return
     */
    @SaCheckLogin
    @PostMapping("")
    public Result<?> addGoodsBrowse(@Validated @RequestBody GoodsBrowseParam addParam) {
        browseService.add(addParam);
        return Result.success();
    }

    /**
     * 商品足迹删除
     *
     * @param param
     * @return
     */
    @SaCheckLogin
    @DeleteMapping("")
    public Result<?> deleteGoodsBrowse(@Validated @RequestBody GoodsBrowseDeleteParam param) {
        browseService.del(param);
        return Result.success();
    }
}
