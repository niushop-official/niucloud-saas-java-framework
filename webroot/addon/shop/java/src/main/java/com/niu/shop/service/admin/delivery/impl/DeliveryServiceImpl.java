package com.niu.shop.service.admin.delivery.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.delivery.ShopDeliveryDeliver;
import com.niu.shop.mapper.delivery.ShopDeliveryDeliverMapper;
import com.niu.shop.service.admin.delivery.IDeliveryService;
import com.niu.shop.service.admin.delivery.IShopDeliveryDeliverService;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryDeliverParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryDeliverSearchParam;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryDeliverInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryDeliverListVo;
import com.niu.shop.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.shop.service.core.delivery.param.DeliverySearchConfigParam;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.shop.service.core.delivery.vo.DeliverySearchConfigVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 配送员实现
 */
@Service
public class DeliveryServiceImpl implements IDeliveryService {
        
    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;

    /**
     * 配送方式
     */
    @Override
    public List<DeliveryConfigVo> getDeliveryList() {
        return coreDeliveryConfigService.getDeliveryList(RequestUtils.siteId());
    }

    /**
     * 配送信息设置
     *
     * @param data
     */
    @Override
    public void setConfig(JSONArray data) {
        coreDeliveryConfigService.setConfig(RequestUtils.siteId(), data);
    }

    /**
     * 获取物流查询接口配置
     */
    @Override
    public DeliverySearchConfigVo getDeliverySearchConfig() {
        return coreDeliveryConfigService.getDeliverySearchConfig(RequestUtils.siteId());
    }

    /**
     * 设置物流查询接口配置
     */
    @Override
    public void setDeliverySearchConfig(DeliverySearchConfigParam data) {
        coreDeliveryConfigService.setDeliverySearchConfig(RequestUtils.siteId(), JSONUtil.parseObj(data));
    }
}
