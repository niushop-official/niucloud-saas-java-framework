package com.niu.core.service.core.pay.driver;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.notify.*;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.bean.transfer.*;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.WebAppEnvs;
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
import com.niu.core.mapper.pay.PayTransferMapper;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.ICoreRefundService;
import com.niu.core.service.core.pay.ICoreTransferSceneService;
import com.niu.core.service.core.pay.ICoreTransferService;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import com.niu.core.service.core.pay.vo.TransferQueryVo;
import com.niu.core.service.core.pay.vo.TransferSceneInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public PayTransferMapper payTransferMapper() {
        return SpringContext.bean(PayTransferMapper.class);
    }

    public ICoreTransferSceneService coreTransferSceneService() {
        return SpringContext.bean(ICoreTransferSceneService.class);
    }

    public BasePay init(JSONObject config) {
        this.config = config;
        payConfig = new WxPayConfig();
        payConfig.setMchId(config.getStr("mch_id", ""));
        payConfig.setApiV3Key(config.getStr("mch_secret_key", ""));
        payConfig.setPrivateCertPath(WebAppEnvs.get().webRootDownResource + config.getStr("mch_public_cert_path", ""));
        payConfig.setPrivateKeyPath(WebAppEnvs.get().webRootDownResource + config.getStr("mch_secret_cert", ""));
        if (ObjectUtil.isNotEmpty(config.getStr("wechat_public_cert_path", "")) && ObjectUtil.isNotEmpty(config.getStr("wechat_public_cert_id", ""))) {
            payConfig.setPublicKeyId(config.getStr("wechat_public_cert_id"));
            payConfig.setPublicKeyPath(WebAppEnvs.get().webRootDownResource + config.getStr("wechat_public_cert_path"));
        }
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
        try {
            TransferBillsNotifyResult result = app().parseTransferBillsNotifyV3Result(getRequestBody(request), getRequestHeader(request));
            TransferBillsNotifyResult.DecryptNotifyResult notifyResult = result.getResult();

            if (result.getRawData().getEventType().equals("MCHTRANSFER.BILL.FINISHED")) {
                TransferNotifyParam notifyParam = new TransferNotifyParam();
                notifyParam.setSiteId(param.getSiteId());
                notifyParam.setTransferNo(notifyResult.getOutBillNo());
                notifyParam.setTransferStatus(handleTransferStatus(notifyResult.getState()));
                coreTransferService().transferNotify(notifyParam);
                return WxPayNotifyV3Response.success("SUCCESS");
            } else {
                log.error("微信转账回调通知失败:{}", result);
                return WxPayNotifyV3Response.fail("微信转账回调通知失败");
            }
        } catch (Exception e) {
            log.error("微信转账回调结果异常,异常原因{}", e.getMessage());
            return WxPayNotifyV3Response.fail(e.getMessage());
        }
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
    public Object transfer(TransferParam param) {
        try {
            PayTransfer transfer = param.getTransfer();
            JSONObject transferPayee = JSONUtil.parseObj(transfer.getTransferPayee());

            TransferSceneInfo transferSceneInfo = coreTransferSceneService().getSceneInfoByType(transfer.getSiteId(), transfer.getTradeType());

            TransferBillsRequest request = new TransferBillsRequest();
            if (transferPayee.getStr("channel", "").equals(ChannelEnum.WECHAT.getCode())) {
                request.setAppid(config.getStr("mp_app_id", ""));
            } else {
                request.setAppid(config.getStr("mini_app_id", ""));
            }
            request.setOutBillNo(transfer.getTransferNo());
            request.setTransferSceneId(transferSceneInfo.getSceneId());
            request.setOpenid(transferPayee.getStr("open_id", ""));
            if (transfer.getMoney().multiply(new BigDecimal(100)).intValue() > 200000) {
                request.setUserName(transfer.getTransferRealname());
            }
            request.setTransferAmount(transfer.getMoney().multiply(new BigDecimal(100)).intValue());
            request.setTransferRemark(ObjectUtil.defaultIfEmpty(transfer.getRemark(), "提现转账"));
            request.setNotifyUrl(param.getNotifyUrl());
            request.setUserRecvPerception(transferSceneInfo.getPerception());

            List<TransferBillsRequest.TransferSceneReportInfo> transferSceneReportInfos = new LinkedList<>();
            JSONObject infos = JSONUtil.parseObj(transferSceneInfo.getInfos());
            for (String type: infos.keySet()) {
                TransferBillsRequest.TransferSceneReportInfo info = new TransferBillsRequest.TransferSceneReportInfo();
                info.setInfoType(type);
                info.setInfoContent(infos.getStr(type, ""));
                transferSceneReportInfos.add(info);
            }
            request.setTransferSceneReportInfos(transferSceneReportInfos);
            TransferBillsResult transferResult = app().getTransferService().transferBills(request);

            TransferNotifyParam notifyParam = new TransferNotifyParam();
            notifyParam.setSiteId(transfer.getSiteId());
            notifyParam.setTransferNo(transfer.getTransferNo());
            notifyParam.setTransferStatus(handleTransferStatus(transferResult.getState()));
            coreTransferService().transferNotify(notifyParam);

            Map<String, Object> result = new HashMap<>();
            result.put("mch_id", config.getStr("mch_id", ""));
            result.put("appid", request.getAppid());
            result.put("out_bill_no", transferResult.getOutBillNo());
            result.put("transfer_bill_no", transferResult.getTransferBillNo());
            result.put("status", transferResult.getState());
            result.put("reason", transferResult.getFailReason());
            result.put("package_info", transferResult.getPackageInfo());
            result.put("extra", JSONUtil.parseObj(result));

            transfer.setOutBatchNo((String) result.get("out_bill_no"));
            transfer.setPackageInfo((String) result.get("package_info"));
            transfer.setExtra(((JSONObject) result.get("extra")).toString());
            payTransferMapper().updateById(transfer);
            return result;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 撤销转账
     * @param transferNo
     * @return
     */
    @Override
    public Object transferCancel(String transferNo) {
        try {
            PayTransfer transfer = payTransferMapper().selectOne(new QueryWrapper<PayTransfer>().eq("transfer_no", transferNo));

            TransferBillsCancelResult result = app().getTransferService().transformBillsCancel(transferNo);

            TransferNotifyParam notifyParam = new TransferNotifyParam();
            notifyParam.setSiteId(transfer.getSiteId());
            notifyParam.setTransferNo(transfer.getTransferNo());
            notifyParam.setTransferStatus(handleTransferStatus(result.getState()));
            coreTransferService().transferNotify(notifyParam);

            return null;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 查询转账单
     * @param transferNo
     * @return
     */
    @Override
    public TransferQueryVo transferQuery(String transferNo) {
        try {
            TransferBillsGetResult result = app().getTransferService().getBillsByOutBillNo(transferNo);
            TransferQueryVo vo = new TransferQueryVo();
            vo.setTransferNo(transferNo);
            vo.setTransferSatus(handleTransferStatus(result.getState()));
            vo.setFailReason(result.getFailReason());
            vo.setOriginal(result);
            return vo;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    private TransferStatusEnum handleTransferStatus(String state) {
        Map<String, TransferStatusEnum> map = new HashMap<>();
        map.put("ACCEPTED", TransferStatusEnum.DEALING);
        map.put("PROCESSING", TransferStatusEnum.DEALING);
        map.put("WAIT_USER_CONFIRM", TransferStatusEnum.WAIT_USER);
        map.put("TRANSFERING", TransferStatusEnum.DEALING);
        map.put("SUCCESS", TransferStatusEnum.SUCCESS);
        map.put("FAIL", TransferStatusEnum.FAIL);
        map.put("CANCELING", TransferStatusEnum.FAIL_ING);
        map.put("CANCELLED", TransferStatusEnum.FAIL);
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
