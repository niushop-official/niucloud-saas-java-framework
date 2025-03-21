package com.niu.shop.service.admin.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.service.admin.diy.IShopDiyFormService;
import com.niu.shop.service.admin.order.IOrderConfigService;
import com.niu.shop.service.core.order.vo.OrderConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class shopDiyFormServiceImpl implements IShopDiyFormService {

    @Resource
    IOrderConfigService orderConfigService;

    @Resource
    ShopGoodsMapper shopGoodsMapper;


    @Override
    public Boolean diyFromDelBefore(Integer formId) {
        OrderConfigVo config = orderConfigService.getConfig();
        if (ObjectUtil.isNotEmpty(config)) {
            if (config.getFormId().equals(formId)) {
                return false;
            }
        }
        Long count = shopGoodsMapper.selectCount(new QueryWrapper<ShopGoods>().eq("form_id", formId));
        if(count == null) count = 0L;
        return count <= 0;
    }
}
