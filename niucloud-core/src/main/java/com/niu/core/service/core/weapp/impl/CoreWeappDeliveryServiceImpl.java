package com.niu.core.service.core.weapp.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.*;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingIsTradeManagedResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.pay.PayChannel;
import com.niu.core.enums.channel.CertEnum;
import com.niu.core.mapper.pay.PayChannelMapper;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.weapp.ICoreWeappDeliveryService;
import com.niu.core.service.core.weapp.param.WeappUploadShippingParam;
import com.niu.core.service.core.weapp.vo.IsTradeManagedVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class CoreWeappDeliveryServiceImpl implements ICoreWeappDeliveryService {

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    private PayChannelMapper payChannelMapper;

    /**
     * 查询小程序是否已开通发货信息管理服务
     *
     * @param siteId
     * @return
     */
    @Override
    public IsTradeManagedVo getIsTradeManaged(Integer siteId) {
        IsTradeManagedVo vo = new IsTradeManagedVo();
        try {
            WxMaService miniapp = WechatUtils.miniapp(siteId);
            String appid = miniapp.getWxMaConfig().getAppid();
            WxMaOrderShippingIsTradeManagedResponse res = miniapp.getWxMaOrderShippingService().isTradeManaged(appid);
            if (!res.getTradeManaged()) {
                System.out.println("小程序未开通发货信息管理服务" + res.getErrMsg());
            }
            vo.setIsTradeManaged(res.getTradeManaged());
        } catch (Exception e) {
        }
        return vo;
    }

    @Override
    public WxMaOrderShippingInfoBaseResponse setMsgJumpPath(Integer siteId, String type) {
        try {
            JSONObject config = getConfig(siteId, type);
            if (ObjectUtils.isEmpty(config)) {
                String path = "app/pages/weapp/order_shipping";
                WxMaOrderShippingInfoBaseResponse response = WechatUtils.miniapp(siteId).getWxMaOrderShippingService().setMsgJumpPath(path);
                if (response.getErrCode() == 0) {
                    setConfig(siteId, type, path);
                }
                return response;
            }

            WxMaOrderShippingInfoBaseResponse response = new WxMaOrderShippingInfoBaseResponse();
            response.setErrCode(0);
            return response;
        } catch (Exception e) {
            WxMaOrderShippingInfoBaseResponse response = new WxMaOrderShippingInfoBaseResponse();
            response.setErrCode(1);
            response.setErrMsg(e.getMessage());
            return response;
        }
    }

    @Override
    public WxMaOrderShippingInfoBaseResponse uploadShippingInfo(Integer siteId, WeappUploadShippingParam shippingData) {
        try {
            log.info("PINTUAN-开始上传微信发货信息 - siteId: {}, data: {}", siteId, shippingData);

            // 获取微信支付配置
            PayChannel payChannel = payChannelMapper.selectOne(new LambdaQueryWrapper<PayChannel>()
                    .eq(PayChannel::getSiteId, siteId)
                    .eq(PayChannel::getType, CertEnum.WECHATPAY.getCode())
                    .eq(PayChannel::getChannel, CertEnum.WEAPP.getCode()));

            String mchId = "";
            if (payChannel != null && !ObjectUtils.isEmpty(payChannel.getConfig())) {
                String config = payChannel.getConfig();
                JSONObject jsonObject = JSONUtil.parseObj(config);
                mchId = jsonObject.getStr("mch_id");
            }

            // 构建微信发货信息对象
            WxMaOrderShippingInfoUploadRequest shippingInfo = new WxMaOrderShippingInfoUploadRequest();

            // 订单信息
            OrderKeyBean orderKey = new OrderKeyBean();
            orderKey.setOrderNumberType(1);
            orderKey.setMchId(mchId);
            orderKey.setOutTradeNo(shippingData.getOutTradeNo());
            shippingInfo.setOrderKey(orderKey);

            // 物流信息
            shippingInfo.setLogisticsType(shippingData.getLogisticsType());
            shippingInfo.setDeliveryMode(shippingData.getDeliveryMode());

            // 处理发货列表
            List<ShippingListBean> wxShippingList = new ArrayList<>();

            for (WeappUploadShippingParam.UploadShippingParam item : shippingData.getShippingList()) {
                ShippingListBean shipping = new ShippingListBean();
                BeanUtils.copyProperties(item, shipping);

                WeappUploadShippingParam.ContactInfo contact = item.getContact();
                if (contact != null) {
                    ContactBean wxContact = new ContactBean();
                    BeanUtils.copyProperties(contact, wxContact);
                    shipping.setContact(wxContact);
                }

                wxShippingList.add(shipping);
            }
            shippingInfo.setShippingList(wxShippingList);

            // 上传时间
            shippingInfo.setUploadTime(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            // 付款人信息
            PayerBean payer = new PayerBean();
            payer.setOpenid(shippingData.getWeappOpenid());
            shippingInfo.setPayer(payer);

            // 发货完成标识
            shippingInfo.setIsAllDelivered(shippingData.getIsAllDelivered());

            log.info("PINTUAN-发货信息录入接口参数: {}", JSONUtil.toJsonStr(shippingInfo));

            // 微信订单录入有时差，延时3秒执行
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 使用微信SDK调用API
            WxMaOrderShippingInfoBaseResponse response = WechatUtils.miniapp(siteId)
                    .getWxMaOrderShippingService()
                    .upload(shippingInfo);

            log.info("PINTUAN-发货信息录入接口返回结果: {}", JSONUtil.toJsonStr(response));

            return response;

        } catch (Exception e) {
            String errorMsg = String.format("uploadShippingInfo报错: %s, File: %s, line: %d",
                    e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
            log.error(errorMsg, e);

            WxMaOrderShippingInfoBaseResponse errorResult = new WxMaOrderShippingInfoBaseResponse();
            errorResult.setErrCode(1);
            errorResult.setErrMsg(errorMsg);
            return errorResult;
        }
    }

    public JSONObject getConfig(Integer siteId, String type) {
        return coreConfigService.getConfigValue(siteId, "WEAPP_ORDER_SHIPPING_CONFIG_" + type);
    }

    public void setConfig(Integer siteId, String type, String path) {
        JSONObject value = new JSONObject();
        value.put("path", path);
        coreConfigService.setConfig(siteId, "WEAPP_ORDER_SHIPPING_CONFIG_" + type, value);
    }

}
