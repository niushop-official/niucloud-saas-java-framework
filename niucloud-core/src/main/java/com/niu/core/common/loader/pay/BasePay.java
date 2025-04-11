package com.niu.core.common.loader.pay;

import cn.hutool.json.JSONObject;
import com.niu.core.common.loader.pay.param.RefundParam;
import com.niu.core.common.loader.pay.param.TransferParam;
import cn.hutool.core.util.ClassLoaderUtil;
import com.niu.core.entity.pay.Pay;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;

import com.niu.core.service.core.pay.vo.TransferQueryVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 基础上传类
 */
public abstract class BasePay {

    protected JSONObject config;

    /**
     * @param driver
     * @return
     */
    public static BasePay forName(String driver) {
        BasePay base = null;
        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(driver);
            base = (BasePay) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base;
    }

    public abstract BasePay init(JSONObject config);

    /**
     * 支付
     * @param param
     */
    public abstract Object pay(PayParam param);

    /**
     * 扫码支付
     */
    public abstract void scan();

    /**
     * 关闭支付
     */
    public abstract void close(Pay pay);

    /**
     * 异步回调
     * @param request
     * @param response
     */
    public abstract Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response);

    /**
     * 转账
     */
    public abstract Object transfer(TransferParam param);

    /**
     * 转账取消
     */
    public abstract Object transferCancel(String transferNo);

    /**
     * 转账查询
     */
    public abstract TransferQueryVo transferQuery(String transferNo);

    /**
     * 退款
     */
    public abstract void refund(RefundParam param);

    /**
     * 过滤支付方式 通过交易类型
     * @param tradeType
     * @return
     */
    public abstract PayTypeVo filterPayTypeByTradeType(String tradeType);

    public JSONObject getConfig() {
        return config;
    }
}
