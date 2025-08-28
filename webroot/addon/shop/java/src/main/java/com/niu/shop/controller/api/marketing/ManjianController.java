package com.niu.shop.controller.api.marketing;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.marketing.IShopManjianService;
import com.niu.shop.service.api.marketing.param.ShopManjianInfoParam;
import com.niu.shop.service.api.marketing.vo.ShopManjianInfoVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.util.ArrayList;

/**
 * 满减控制器
 */

@RestController
@RequestMapping("api/shop")
public class ManjianController {

    @Resource
    IShopManjianService shopManjianService;

    /**
     * 满减信息
     *
     * @return
     */
    @GetMapping("/manjian/info")
    public Result getManjianInfo(ShopManjianInfoParam shopManjianInfoParam)  {
        return Result.success(ObjectUtil.defaultIfNull(shopManjianService.getManjianInfo(shopManjianInfoParam), new ArrayList<>()));
    }

}
