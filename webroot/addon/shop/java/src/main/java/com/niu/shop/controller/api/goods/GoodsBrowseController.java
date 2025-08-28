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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Enumeration;
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
     * 商品足迹删除 解决参数是?goods_ids%5B0%5D=41格式的问题
     * @param request
     * @return
     */
    @SaCheckLogin
    @DeleteMapping("")
    public Result<?> deleteGoodsBrowse(HttpServletRequest request) {
        List<Integer> goodsIds = new ArrayList<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            // 匹配所有以 "goods_ids[" 开头的参数
            if (paramName.startsWith("goods_ids[")) {
                String value = request.getParameter(paramName);
                goodsIds.add(Integer.parseInt(value));
            }
        }
        if(goodsIds.size() >0 ){
            GoodsBrowseDeleteParam goodsBrowseDeleteParam = new GoodsBrowseDeleteParam();
            goodsBrowseDeleteParam.setGoodsIds(goodsIds);
            browseService.del(goodsBrowseDeleteParam);
        }
        return Result.success();
    }
}
