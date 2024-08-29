package com.niu.core.service.core.pay.driver;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.binarywang.wxpay.bean.notify.*;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.bean.transfer.TransferBatchesRequest;
import com.github.binarywang.wxpay.bean.transfer.TransferBatchesResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class Wechatpay extends BasePay {

    private WxPayConfig payConfig;

    private JSONObject config;

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
        this.config = config;
        payConfig = new WxPayConfig();
        payConfig.setMchId(config.getStr("mch_id", ""));
        payConfig.setApiV3Key(config.getStr("mch_secret_key", ""));
        payConfig.setPrivateCertPath(WebAppEnvs.get().webRootDownResource + config.getStr("mch_public_cert_path", ""));
        payConfig.setPrivateKeyPath(WebAppEnvs.get().webRootDownResource + config.getStr("mch_secret_cert", ""));
        return this;
    }

    private WxPayService app() {
        WxPayServiceImpl payService = new WxPayServiceImpl();
        payService.setConfig(payConfig);
        return payService;
    }

    public Object pay(PayParam param) {
        if (RequestUtils.channel().equals(ChannelEnum.H5.getCode())) {
            return h5(param);
        }
        if (RequestUtils.channel().equals(ChannelEnum.WECHAT.getCode())) {
            return mp(param);
        }
        if (RequestUtils.channel().equals(ChannelEnum.WEAPP.getCode())) {
            return miniapp(param);
        }
        if (RequestUtils.channel().equals(ChannelEnum.PC.getCode())) {
            return web(param);
        }
        return null;
    }

    private Object mp(PayParam param) {
        try {
            payConfig.setAppId(config.getStr("mp_app_id", ""));

            Pay pay = param.getPay();

            WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();
            request.setOutTradeNo(pay.getOutTradeNo());
            request.setPayer(new WxPayUnifiedOrderV3Request.Payer().setOpenid(param.getOpenid()));
            request.setDescription(pay.getBody());
            request.setNotifyUrl(param.getNotifyUrl());
            request.setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(pay.getMoney().multiply(new BigDecimal(100)).intValue()));

            WxPayUnifiedOrderV3Result.JsapiResult result = app().createOrderV3(TradeTypeEnum.JSAPI, request);
            Map<String, String> data = new HashMap<String, String>();
            data.put("appId", result.getAppId());
            data.put("timeStamp", result.getTimeStamp());
            data.put("nonceStr", result.getNonceStr());
            data.put("package", result.getPackageValue());
            data.put("signType", result.getSignType());
            data.put("paySign", result.getPaySign());
            return data;
        } catch (Exception e) {
            log.error("微信公众号支付异常:{}", e);
            throw new CommonException(e.getMessage());
        }
    }

    private Object miniapp(PayParam param) {
        try {
            payConfig.setAppId(config.getStr("mini_app_id", ""));

            Pay pay = param.getPay();

            WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();
            request.setOutTradeNo(pay.getOutTradeNo());
            request.setPayer(new WxPayUnifiedOrderV3Request.Payer().setOpenid(param.getOpenid()));
            request.setDescription(pay.getBody());
            request.setNotifyUrl(param.getNotifyUrl());
            request.setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(pay.getMoney().multiply(new BigDecimal(100)).intValue()));

            WxPayUnifiedOrderV3Result.JsapiResult result = app().createOrderV3(TradeTypeEnum.JSAPI, request);
            Map<String, String> data = new HashMap<String, String>();
            data.put("appId", result.getAppId());
            data.put("timeStamp", result.getTimeStamp());
            data.put("nonceStr", result.getNonceStr());
            data.put("package", result.getPackageValue());
            data.put("signType", result.getSignType());
            data.put("paySign", result.getPaySign());
            return data;
        } catch (Exception e) {
            log.error("微信小程序支付异常:{}", e);
            throw new CommonException(e.getMessage());
        }
    }

    private Object h5(PayParam param) {
        try {
            payConfig.setAppId(config.getStr("mp_app_id", ""));

            Pay pay = param.getPay();

            WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();

            WxPayUnifiedOrderV3Request.SceneInfo sceneInfo = new WxPayUnifiedOrderV3Request.SceneInfo();
            sceneInfo.setPayerClientIp(RequestUtils.ip());
            sceneInfo.setH5Info(new WxPayUnifiedOrderV3Request.H5Info().setType("Wap"));

            request.setOutTradeNo(pay.getOutTradeNo());
            request.setDescription(pay.getBody());
            request.setNotifyUrl(param.getNotifyUrl());
            request.setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(pay.getMoney().multiply(new BigDecimal(100)).intValue()));
            request.setSceneInfo(sceneInfo);

            String h5Url = app().createOrderV3(TradeTypeEnum.H5, request);

            Map<String, String> data = new HashMap<>();
            data.put("h5_url", h5Url);
            return data;
        } catch (Exception e) {
            log.error("微信H5支付异常:{}", e);
            throw new CommonException(e.getMessage());
        }
    }

    private Object web(PayParam param) {
        try {
            Pay pay = param.getPay();

            WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();
            request.setOutTradeNo(pay.getOutTradeNo());
            request.setPayer(new WxPayUnifiedOrderV3Request.Payer().setOpenid(param.getOpenid()));
            request.setDescription(pay.getBody());
            request.setNotifyUrl(param.getNotifyUrl());
            request.setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(pay.getMoney().multiply(new BigDecimal(100)).intValue()));

            return app().createOrderV3(TradeTypeEnum.NATIVE, request);
        } catch (Exception e) {
            log.error("微信native支付异常:{}", e);
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
            app().closeOrderV3(pay.getOutTradeNo());
        } catch (Exception e) {
            log.error("微信支付关闭结果异常,异常原因{}", e.getMessage());
        }
    }

    /**
     * 异步回调
     *
     * @param request
     * @param response
     */
    public Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
        switch (param.getAction()) {
            case "pay":
                return payNotify(param, request, response);
            case "refund":
                return refundNotify(param, request, response);
            case "transfer":
                return transferNotify(param, request, response);
            default:
                return null;
        }
    }

    private String payNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
        try {
            WxPayNotifyV3Result result = app().parseOrderNotifyV3Result(getRequestBody(request), getRequestHeader(request));
            WxPayNotifyV3Result.DecryptNotifyResult notifyResult = result.getResult();

            if (result.getRawData().getEventType().equals("TRANSACTION.SUCCESS")) {
                PayNotifyParam successParam = new PayNotifyParam();
                successParam.setOutTradeNo(notifyResult.getOutTradeNo());
                successParam.setSiteId(param.getSiteId());
                successParam.setType(param.getType());
                successParam.setTradeNo(notifyResult.getTransactionId());
                successParam.setPayStatus(handlePayStatus(notifyResult.getTradeState()));
                corePayService().payNotify(successParam);

                return WxPayNotifyV3Response.success("SUCCESS");
            } else {
                log.error("微信支付回调通知失败:{}", result);
                return WxPayNotifyV3Response.fail("微信支付回调通知失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("微信支付回调结果异常,异常原因{}", e.getMessage());
            return WxPayNotifyV3Response.fail(e.getMessage());
        }
    }

    private OnliepayStatusEnum handlePayStatus(String state) {
        Map<String, OnliepayStatusEnum> map = new HashMap<>();
        map.put("SUCCESS", OnliepayStatusEnum.SUCCESS);
        map.put("REFUND", OnliepayStatusEnum.REFUND);
        map.put("NOTPAY", OnliepayStatusEnum.NOTPAY);
        map.put("CLOSED", OnliepayStatusEnum.CLOSED);
        map.put("REVOKED", OnliepayStatusEnum.REVOKED);
        map.put("USERPAYING", OnliepayStatusEnum.USERPAYING);
        map.put("PAYERROR", OnliepayStatusEnum.PAYERROR);
        return map.get(state);
    }

    private Object refundNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
        try {
            WxPayRefundNotifyV3Result result = app().parseRefundNotifyV3Result(getRequestBody(request), getRequestHeader(request));
            WxPayRefundNotifyV3Result.DecryptNotifyResult notifyResult = result.getResult();

            if (result.getRawData().getEventType().equals("REFUND.SUCCESS")) {
                RefundNotifyParam notifyParam = new RefundNotifyParam();
                notifyParam.setSiteId(param.getSiteId());
                notifyParam.setRefundNo(notifyResult.getOutRefundNo());
                notifyParam.setOutTradeNo(notifyResult.getOutTradeNo());
                notifyParam.setRefundStatus(handleRefundStatus(notifyResult.getRefundStatus()));
                notifyParam.setType(param.getType());
                coreRefundService().refundNotify(notifyParam);

                return WxPayNotifyV3Response.success("SUCCESS");
            } else {
                log.error("微信支付回调通知失败:{}", result);
                return WxPayNotifyV3Response.fail("微信退款回调通知失败");
            }
        } catch (Exception e) {
            log.error("微信退款回调结果异常,异常原因{}", e.getMessage());
            return WxPayNotifyV3Response.fail(e.getMessage());
        }
    }

    private Object transferNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
//        try {
//            WxPayTransferBatchesNotifyV3Result result = app().parseTransferBatchesNotifyV3Result(getRequestBody(request), getRequestHeader(request));
//            WxPayTransferBatchesNotifyV3Result.DecryptNotifyResult notifyResult = result.getResult();
//
//            if (result.getRawData().getEventType().equals("MCHTRANSFER.BATCH.FINISHED")) {
//                TransferNotifyParam notifyParam = new TransferNotifyParam();
//                notifyParam.setSiteId(param.getSiteId());
//                notifyParam.setTransferNo(notifyResult.getOutBatchNo());
//                notifyParam.setTransferStatus(handleTransferStatus(notifyResult.getBatchStatus()));
//                coreTransferService.transferNotify(notifyParam);
//
//                return WxPayNotifyV3Response.success("SUCCESS");
//            } else {
//                log.error("微信转账回调通知失败:{}", result);
//                return WxPayNotifyV3Response.fail("微信转账回调通知失败");
//            }
//        } catch (Exception e) {
//            log.error("微信转账回调结果异常,异常原因{}", e.getMessage());
//            return WxPayNotifyV3Response.fail(e.getMessage());
//        }
        return null;
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder data = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            data.append(line);
        }
        reader.close();
        return data.toString();
    }

    /**
     * 获取回调请求头：签名相关
     *
     * @param request HttpServletRequest
     * @return SignatureHeader
     */
    private SignatureHeader getRequestHeader(HttpServletRequest request) {
        // 获取通知签名
        String signature = request.getHeader("Wechatpay-Signature");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String serial = request.getHeader("Wechatpay-Serial");
        String timestamp = request.getHeader("Wechatpay-Timestamp");

        SignatureHeader signatureHeader = new SignatureHeader();
        signatureHeader.setSignature(signature);
        signatureHeader.setNonce(nonce);
        signatureHeader.setSerial(serial);
        signatureHeader.setTimeStamp(timestamp);
        return signatureHeader;
    }

    /**
     * 转账
     */
    public void transfer(TransferParam param) {
        try {
            PayTransfer transfer = param.getTransfer();

            List<TransferBatchesRequest.TransferDetail> transferDetailList = new LinkedList<>();
            TransferBatchesRequest.TransferDetail transferDetail = new TransferBatchesRequest.TransferDetail();
            transferDetail.setOutDetailNo(transfer.getTransferNo());
            transferDetail.setTransferAmount(transfer.getMoney().multiply(new BigDecimal(100)).intValue());
            transferDetail.setOpenid(transfer.getOpenid());
            transferDetail.setTransferRemark(transfer.getRemark());
            if (transfer.getMoney().multiply(new BigDecimal(100)).intValue() > 200000) {
                transferDetail.setUserName(transfer.getTransferRealname());
            }

            TransferBatchesRequest request = new TransferBatchesRequest();
            request.setAppid(config.getStr("mp_app_id", ""));
            request.setOutBatchNo(transfer.getTransferNo());
            request.setBatchName("");
            request.setBatchRemark(transfer.getRemark());
            request.setTotalAmount(transfer.getMoney().multiply(new BigDecimal(100)).intValue());
            request.setTotalNum(1);
            request.setTransferDetailList(transferDetailList);
            app().getConfig().setNotifyUrl(param.getNotifyUrl());
            TransferBatchesResult result = app().getTransferService().transferBatches(request);

            TransferNotifyParam notifyParam = new TransferNotifyParam();
            notifyParam.setSiteId(transfer.getSiteId());
            notifyParam.setTransferNo(transfer.getTransferNo());
            notifyParam.setTransferStatus(handleTransferStatus(result.getBatchStatus()));
            coreTransferService().transferNotify(notifyParam);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    private TransferStatusEnum handleTransferStatus(String state) {
        Map<String, TransferStatusEnum> map = new HashMap<>();
        map.put("FINISHED", TransferStatusEnum.SUCCESS);
        map.put("PROCESSING", TransferStatusEnum.DEALING);
        map.put("CLOSED", TransferStatusEnum.FAIL);
        return map.get(state);
    }

    /**
     * 退款
     */
    public void refund(RefundParam param) {
        try {
            WxPayRefundV3Request request = new WxPayRefundV3Request();
            request.setOutTradeNo(param.getOutTradeNo());
            request.setOutRefundNo(param.getRefundNo());
            request.setAmount(new WxPayRefundV3Request.Amount()
                    .setRefund(param.getMoney().multiply(new BigDecimal(100)).intValue())
                    .setTotal(param.getTotal().multiply(new BigDecimal(100)).intValue())
                    .setCurrency("CNY"));
            request.setNotifyUrl(param.getNotifyUrl());

            WxPayRefundV3Result result = app().refundV3(request);

            RefundNotifyParam notifyParam = new RefundNotifyParam();
            BeanUtil.copyProperties(param, notifyParam);
            notifyParam.setType("wechatpay");
            notifyParam.setRefundStatus(handleRefundStatus(result.getStatus()));
            coreRefundService().refundNotify(notifyParam);
        } catch (Exception e) {
            log.error("微信退款结果异常,异常原因{}", e.getMessage());
            throw new CommonException(e.getMessage());
        }
    }

    private RefundTransferStatusEnum handleRefundStatus(String state) {
        Map<String, RefundTransferStatusEnum> map = new HashMap<>();
        map.put("SUCCESS", RefundTransferStatusEnum.SUCCESS);
        map.put("CLOSED", RefundTransferStatusEnum.CLOSED);
        map.put("PROCESSING", RefundTransferStatusEnum.PROCESSING);
        map.put("ABNORMAL", RefundTransferStatusEnum.ABNORMAL);
        return map.get(state);
    }

    /**
     * 过滤支付方式 通过交易类型
     *
     * @param tradeType
     * @return
     */
    public PayTypeVo filterPayTypeByTradeType(String tradeType) {
        PayTypeVo vo = JSONUtil.toBean(PayTypeEnum.getType().getJSONObject("wechatpay"), PayTypeVo.class);
        return vo;
    }
}
