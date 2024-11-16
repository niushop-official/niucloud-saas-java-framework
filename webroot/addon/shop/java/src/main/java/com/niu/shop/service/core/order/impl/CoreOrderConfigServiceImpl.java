package com.niu.shop.service.core.order.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import com.niu.shop.entity.order.config.*;
import com.niu.shop.service.core.order.ICoreOrderConfigService;
import com.niu.shop.service.core.order.param.OrderConfigParam;
import com.niu.shop.service.core.order.vo.OrderConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
class CoreOrderConfigServiceImpl implements ICoreOrderConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public void setConfig(Integer siteId, OrderConfigParam orderConfigParam) {
        coreConfigService.setConfig(siteId, "SHOP_ORDER_CONFIG", JSONUtil.parseObj(orderConfigParam));

    }

    @Override
    public OrderConfigVo getConfig(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, "SHOP_ORDER_CONFIG");
        OrderConfigParam orderConfigParam = new OrderConfigParam();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            orderConfigParam = JSONUtil.toBean(coreSysConfigVo.getValueJson(), OrderConfigParam.class);
        }

        OrderConfigVo vo = new OrderConfigVo();
        //订单关闭配置
        ShopOrderClose shopOrderClose = new ShopOrderClose();
        shopOrderClose.setIsClose(orderConfigParam.getIsClose());
        shopOrderClose.setCloseLength(orderConfigParam.getCloseLength());
        vo.setCloseOrderInfo(shopOrderClose);

        //订单完成配置
        ShopOrderFinish shopOrderFinish = new ShopOrderFinish();
        shopOrderFinish.setIsFinish(orderConfigParam.getIsFinish());
        shopOrderFinish.setFinishLength(orderConfigParam.getFinishLength());
        vo.setConfirm(shopOrderFinish);

        //订单退款配置
        ShopOrderRefund shopOrderRefund = new ShopOrderRefund();
        shopOrderRefund.setNoAllowRefund(orderConfigParam.getNoAllowRefund());
        shopOrderRefund.setRefundLength(orderConfigParam.getRefundLength());
        vo.setRefund(shopOrderRefund);

        //订单评价配置
        ShopOrderEvaluate shopOrderEvaluate = new ShopOrderEvaluate();
        shopOrderEvaluate.setIsEvaluate(orderConfigParam.getIsEvaluate());
        shopOrderEvaluate.setEvaluateIsToExamine(orderConfigParam.getEvaluateIsToExamine());
        shopOrderEvaluate.setEvaluateIsShow(orderConfigParam.getEvaluateIsShow());
        vo.setEvaluate(shopOrderEvaluate);

        //订单发票配置
        ShopOrderInvoice shopOrderInvoice = new ShopOrderInvoice();
        shopOrderInvoice.setIsInvoice(orderConfigParam.getIsInvoice());
        shopOrderInvoice.setInvoiceType(orderConfigParam.getInvoiceType());
        shopOrderInvoice.setInvoiceContent(orderConfigParam.getInvoiceContent());
        vo.setInvoice(shopOrderInvoice);
        return vo;
    }

    @Override
    public ShopOrderFinish orderConfirm(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, "SHOP_ORDER_CONFIG");
        ShopOrderFinish shopOrderFinish = new ShopOrderFinish();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            shopOrderFinish = JSONUtil.toBean(coreSysConfigVo.getValueJson(), ShopOrderFinish.class);
        }

        return shopOrderFinish;
    }

    @Override
    public ShopOrderRefund orderRefund(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, "SHOP_ORDER_CONFIG");
        ShopOrderRefund shopOrderRefund = new ShopOrderRefund();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            shopOrderRefund = JSONUtil.toBean(coreSysConfigVo.getValueJson(), ShopOrderRefund.class);
        }

        return shopOrderRefund;
    }

    @Override
    public ShopOrderInvoice invoice(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, "SHOP_ORDER_CONFIG");
        ShopOrderInvoice shopOrderInvoice = new ShopOrderInvoice();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            shopOrderInvoice = JSONUtil.toBean(coreSysConfigVo.getValueJson(), ShopOrderInvoice.class);
        }

        return shopOrderInvoice;
    }

    @Override
    public ShopOrderEvaluate getEvaluateConfig(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, "SHOP_ORDER_CONFIG");
        ShopOrderEvaluate shopOrderEvaluate = new ShopOrderEvaluate();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            shopOrderEvaluate = JSONUtil.toBean(coreSysConfigVo.getValueJson(), ShopOrderEvaluate.class);
        }

        return shopOrderEvaluate;
    }
}
