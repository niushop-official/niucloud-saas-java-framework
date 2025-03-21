package com.niu.shop.service.admin.delivery.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.delivery.ShopDeliveryLocalDelivery;
import com.niu.shop.mapper.delivery.ShopDeliveryLocalDeliveryMapper;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryLocalDeliveryParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryLocalDeliverySearchParam;
import com.niu.shop.service.admin.delivery.IShopDeliveryLocalDeliveryService;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryLocalDeliveryInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryLocalDeliveryListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;


/**
 * 同城配送实现
 */
@Service
public class ShopDeliveryLocalDeliveryServiceImpl implements IShopDeliveryLocalDeliveryService {
        
    @Resource
    ShopDeliveryLocalDeliveryMapper shopDeliveryLocalDeliveryMapper;

    /**
     * 同城配送详情
     * @return ShopDeliveryLocalDeliveryInfoVo
     */
    @Override
    public ShopDeliveryLocalDeliveryInfoVo getLocal() {
        ShopDeliveryLocalDelivery model = shopDeliveryLocalDeliveryMapper.selectOne(
                new QueryWrapper<ShopDeliveryLocalDelivery>()
                    .eq("site_id", RequestUtils.siteId())
                    .last("limit 1"));

        if (model == null) return null;

        ShopDeliveryLocalDeliveryInfoVo vo = new ShopDeliveryLocalDeliveryInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 同城配送设置
     *
     * @param param 参数
     */
    @Override
    public void setLocal(ShopDeliveryLocalDeliveryParam param) {
        shopDeliveryLocalDeliveryMapper.delete(new QueryWrapper<ShopDeliveryLocalDelivery>().eq("site_id", RequestUtils.siteId()));

        ShopDeliveryLocalDelivery model = new ShopDeliveryLocalDelivery();
        BeanUtils.copyProperties(param, model);
        model.setSiteId(RequestUtils.siteId());
        model.setDeliveryType(param.getDeliveryType().toString());
        model.setCenter(param.getCenter().toString());
        model.setArea(param.getArea().toString());
        shopDeliveryLocalDeliveryMapper.insert(model);
    }

}
