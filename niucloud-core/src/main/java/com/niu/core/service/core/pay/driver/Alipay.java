package com.niu.core.service.core.pay.driver;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.common.models.AlipayTradeRefundResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.alipay.easysdk.payment.wap.models.AlipayTradeWapPayResponse;
import com.alipay.easysdk.util.generic.models.AlipayOpenApiGenericResponse;
import com.niu.core.component.context.SpringContext;
import com.niu.core.component.context.WebAppEnvs;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.pay.BasePay;
import com.niu.core.common.loader.pay.param.*;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.OnliepayStatusEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.pay.RefundTransferStatusEnum;
import com.niu.core.enums.pay.TransferStatusEnum;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.ICoreRefundService;
import com.niu.core.service.core.pay.ICoreTransferService;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Component
public class Alipay extends BasePay {

    public ICorePayService corePayService() {
        return SpringContext.bean(ICorePayService.class);
    }

    public ICoreRefundService coreRefundService() {
        return SpringContext.bean(ICoreRefundService.class);
    }

    public ICoreTransferService coreTransferService() {
        return SpringContext.bean(ICoreTransferService.class);
    }

    public BasePay init(JSONObject config) {
        Config option = new Config();
        option.protocol = "https";
        option.gatewayHost = "openapi.alipay.com";
        option.signType = "RSA2";
        option.appId = config.getStr("app_id", "");
        option.merchantPrivateKey = config.getStr("app_secret_cert", "");
        option.merchantCertPath = WebAppEnvs.get().webRootDownResource + config.getStr("app_public_cert_path", "");
        option.alipayCertPath = WebAppEnvs.get().webRootDownResource + config.getStr("alipay_public_cert_path", "");
        option.alipayRootCertPath = WebAppEnvs.get().webRootDownResource + config.getStr("alipay_root_cert_path", "");
        Factory.setOptions(option);
        return this;
    }

    /**
     * 支付
     * @param param
     */
    public Object pay(PayParam param) {
        if (RequestUtils.channel().equals(ChannelEnum.H5.getCode()) || RequestUtils.channel().equals(ChannelEnum.WECHAT.getCode())) {
            return h5(param);
        }
        if (RequestUtils.channel().equals(ChannelEnum.PC.getCode())) {
            return web(param);
        }
        return null;
    }

    private Object h5(PayParam param) {
        try {
            Pay pay = param.getPay();
            AlipayTradeWapPayResponse response = Factory.Payment.Wap().asyncNotify(param.getNotifyUrl()).pay(
                    pay.getBody(),
                    pay.getOutTradeNo(),
                    pay.getMoney().toString(),
                    param.getQuitUrl(),
                    param.getRefundUrl()
            );
            Map<String, String> data = new HashMap<>();
            data.put("url", response.getBody());
            return data;
        } catch (Exception e) {
            log.error("支付宝H5支付异常:{}", e);
            throw new CommonException(e.getMessage());
        }
    }

    private Object web(PayParam param) {
        try {
            Pay pay = param.getPay();
            AlipayTradePagePayResponse response = Factory.Payment.Page().asyncNotify(param.getNotifyUrl()).pay(
                    pay.getBody(),
                    pay.getOutTradeNo(),
                    pay.getMoney().toString(),
                    param.getRefundUrl()
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("支付宝pc支付异常:{}", e);
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 扫码支付
     */
    public void scan() {

    }

    /**
     * 关闭支付
     */
    public void close(Pay pay) {
        try {
            Factory.Payment.Common().close(pay.getOutTradeNo());
        } catch (Exception e) {
            log.error("支付宝支付关闭异常:{}", e);
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 异步回调
     * @param request
     * @param response
     */
    public Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> params = new HashMap<String, String>();
        try {
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }
            if (!Factory.Payment.Common().verifyNotify(params)) {
                log.error("支付宝异步回调验签失败");
                return "fail";
            }
        } catch (Exception e) {
            log.error("支付宝异步结果异常,异常原因{}", e.getMessage());
            return "fail";
        }

        switch (param.getAction()) {
            case "pay":
                return payNotify(param, params);
            case "refund":
                return refundNotify(param, params);
            default:
                return null;
        }
    }

    private Object payNotify(PayAsyncNotifyParam param, Map<String, String> params) {
        try {
            PayNotifyParam successParam = new PayNotifyParam();
            successParam.setOutTradeNo(params.get("out_trade_no"));
            successParam.setSiteId(param.getSiteId());
            successParam.setType(param.getType());
            successParam.setTradeNo(params.get("trade_no"));
            successParam.setPayStatus(handlePayStatus(params.get("trade_status")));
            corePayService().payNotify(successParam);
            return "success";
        } catch (Exception e) {
            log.error("支付宝支付回调结果异常,异常原因{}", e.getMessage());
            return "fail";
        }
    }

    private Object refundNotify(PayAsyncNotifyParam param, Map<String, String> params) {
        try {
            RefundNotifyParam notifyParam = new RefundNotifyParam();
            BeanUtil.copyProperties(param, notifyParam);
            notifyParam.setOutTradeNo(params.get("out_trade_no"));
            notifyParam.setRefundStatus(handleRefundStatus(params.get("dback_status")));
            notifyParam.setRefundNo(params.get("out_request_no"));
            notifyParam.setType(param.getType());
            coreRefundService().refundNotify(notifyParam);
            return "success";
        } catch (Exception e) {
            log.error("支付宝退款回调结果异常,异常原因{}", e.getMessage());
            return "fail";
        }
    }

    private OnliepayStatusEnum handlePayStatus(String state) {
        Map<String, OnliepayStatusEnum> map = new HashMap<>();
        map.put("TRADE_SUCCESS", OnliepayStatusEnum.SUCCESS);
        map.put("WAIT_BUYER_PAY", OnliepayStatusEnum.NOTPAY);
        map.put("TRADE_CLOSED", OnliepayStatusEnum.CLOSED);
        return map.get(state);
    }

    /**
     * 转账
     */
    public void transfer(TransferParam param) {
        try {
            PayTransfer transfer = param.getTransfer();

            Map<String, String> textParams = new HashMap<>();
            Map<String, Object> bizParams = new HashMap<>();
            bizParams.put("out_biz_no", transfer.getTransferNo());
            bizParams.put("trans_amount", transfer.getMoney());
            bizParams.put("biz_scene", "DIRECT_TRANSFER");
            bizParams.put("product_code", "TRANS_ACCOUNT_NO_PWD");
            bizParams.put("order_title", transfer.getRemark().isEmpty() ? "账户提现" : transfer.getRemark());
            Map<String, Object> payeeInfo = new HashMap<>();
            payeeInfo.put("identity", transfer.getTransferAccount());
            payeeInfo.put("identity_type", "ALIPAY_LOGON_ID");
            payeeInfo.put("name", transfer.getTransferRealname());
            bizParams.put("payee_info", payeeInfo);
            AlipayOpenApiGenericResponse response = Factory.Util.Generic().asyncNotify(param.getNotifyUrl()).execute("alipay.fund.trans.uni.transfer", textParams, bizParams);
            JSONObject result = JSONUtil.parseObj(response.getHttpBody());

            TransferNotifyParam notifyParam = new TransferNotifyParam();
            notifyParam.setSiteId(transfer.getSiteId());
            notifyParam.setTransferNo(transfer.getTransferNo());
            if (result.getStr("code").equals("10000")) {
                notifyParam.setTransferStatus(handleTransferStatus(result.getStr("status")));
            } else {
                notifyParam.setTransferStatus(handleTransferStatus("FAIL"));
                notifyParam.setFailReason(result.getStr("sub_msg"));
            }
            coreTransferService().transferNotify(notifyParam);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    private TransferStatusEnum handleTransferStatus(String state) {
        Map<String, TransferStatusEnum> map = new HashMap<>();
        map.put("SUCCESS", TransferStatusEnum.SUCCESS);
        map.put("FAIL", TransferStatusEnum.FAIL);
        return map.get(state);
    }

    /**
     * 退款
     */
    public void refund(RefundParam param) {
        try {
            AlipayTradeRefundResponse response = Factory.Payment.Common().optional("out_request_no", param.getRefundNo()).refund(param.getOutTradeNo(), param.getMoney().toString());
            RefundNotifyParam notifyParam = new RefundNotifyParam();
            BeanUtil.copyProperties(param, notifyParam);
            notifyParam.setRefundStatus(handleRefundStatus(response.getFundChange()));
            notifyParam.setType("alipay");
            coreRefundService().refundNotify(notifyParam);
        } catch (Exception e) {
            log.error("支付宝支付关闭异常:{}", e);
            throw new CommonException(e.getMessage());
        }
    }

    private RefundTransferStatusEnum handleRefundStatus(String state) {
        Map<String, RefundTransferStatusEnum> map = new HashMap<>();
        map.put("Y", RefundTransferStatusEnum.SUCCESS);
        map.put("N", RefundTransferStatusEnum.PROCESSING);
        map.put("S", RefundTransferStatusEnum.SUCCESS);
        map.put("F", RefundTransferStatusEnum.ABNORMAL);
        return map.get(state);
    }

    /**
     * 过滤支付方式 通过交易类型
     * @param tradeType
     * @return
     */
    public PayTypeVo filterPayTypeByTradeType(String tradeType) {
        PayTypeVo vo = JSONUtil.toBean(PayTypeEnum.getType().getJSONObject("alipay"), PayTypeVo.class);
        return vo;
    }
}
