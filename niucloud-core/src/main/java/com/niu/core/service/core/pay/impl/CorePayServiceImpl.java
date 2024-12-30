package com.niu.core.service.core.pay.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.pay.BasePay;
import com.niu.core.common.loader.pay.PayLoader;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.pay.OnliepayStatusEnum;
import com.niu.core.enums.pay.PayStatusEnum;
import com.niu.core.event.pay.PayCloseEvent;
import com.niu.core.event.pay.PayCreateEventDefiner;
import com.niu.core.event.pay.PaySuccessEvent;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayChannelService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.sys.ICorePrinterService;
import com.niu.core.service.core.sys.param.SysPrinterPrintTicketParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 支付记录实现
 */
@Service
public class CorePayServiceImpl implements ICorePayService {

    @Resource
    PayMapper payMapper;

    @Resource
    ICorePayChannelService corePayChannelService;

    @Resource
    ICorePrinterService corePrinterService;

    @Resource
    Cached cached;

    /**
     * 支付
     *
     * @param param
     * @return
     */
    @Override
    @Transactional
    public Object pay(PayParam param) {
        Pay pay = this.checkOrCreate(param.getSiteId(), param.getTradeType(), param.getTradeId());
        if (pay.getBody().length() > 15) {
            pay.setBody(pay.getBody().substring(0, 15) + "...");
        }
        param.setPay(pay);
        param.setNotifyUrl(buildNotifyUrl(param.getSiteId(), param.getChannel(), param.getType(), "pay"));
        return this.driver(param.getSiteId(), param.getChannel(), param.getType()).pay(param);
    }

    /**
     * 异步回调
     */
    public Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
        return this.driver(param.getSiteId(), param.getChannel(), param.getType()).asyncNotify(param, request, response);
    }

    /**
     * 支付回调处理
     *
     * @param param
     */
    @Transactional
    public void payNotify(PayNotifyParam param) {
        Pay pay = this.findPayInfoByOutTradeNo(param.getSiteId(), param.getOutTradeNo());
        if (pay == null) throw new CommonException("未获取到支付单据");
        if (pay.getStatus().equals(PayStatusEnum.STATUS_FINISH.getCode())) throw new CommonException("单据已支付");
        if (pay.getStatus().equals(PayStatusEnum.STATUS_CANCLE.getCode())) throw new CommonException("当前支付已取消");

        // 支付成功
        if (param.getPayStatus().getStatus().equals(OnliepayStatusEnum.SUCCESS.getStatus())) {
            this.paySuccess(pay, param);
        }
        // 支付关闭
        if (param.getPayStatus().getStatus().equals(OnliepayStatusEnum.CLOSED.getStatus())) {
            this.payClose(param.getSiteId(), pay);
        }
    }

    public String buildNotifyUrl(Integer siteId, String channel, String type, String action) {
        String[] param = {siteId.toString(), channel, type, action};
        String baseUrl = "";
        if (RequestUtils.handler() != null) {
            baseUrl = RequestUtils.getDomain(true);
            cached.put("payBaseurl", baseUrl);
        } else if (cached.get("payBaseurl") != null) {
            baseUrl = (String) cached.get("payBaseurl");
        } else {
            throw new CommonException("build notify url error");
        }
        return baseUrl + "/api/pay/notify/" + String.join("/", param);
    }

    /**
     * 检测并创建支付单据
     *
     * @param siteId
     * @param tradeType
     * @param tradeId
     */
    public Pay checkOrCreate(Integer siteId, String tradeType, Integer tradeId) {
        Pay pay = this.findPayInfoByTrade(siteId, tradeType, tradeId);
        if (pay == null) {
            return this.createByTrade(siteId, tradeType, tradeId);
        }
        if (pay.getStatus().equals(PayStatusEnum.STATUS_FINISH.getCode())) throw new CommonException("当前支付已完成");
        if (pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) || pay.getStatus().equals(PayStatusEnum.STATUS_CANCLE.getCode())) {
            if (pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode())) {
                this.close(siteId, pay);
            }
            return this.createByTrade(siteId, tradeType, tradeId);
        }
        return pay;
    }

    /**
     * 支付驱动
     *
     * @param siteId
     * @param type
     * @param channel
     * @return
     */
    public BasePay driver(Integer siteId, String channel, String type) {
        JSONObject config = ObjectUtil.defaultIfNull(corePayChannelService.getConfigByChannelAndType(siteId, channel, type), new JSONObject());
        return PayLoader.getDriver(type).init(config);
    }

    /**
     * 通过业务类型和id查询有效的支付单据
     *
     * @param siteId
     * @param tradeType
     * @param tradeId
     */
    public Pay findPayInfoByTrade(Integer siteId, String tradeType, Integer tradeId) {
        return payMapper.selectOne(new QueryWrapper<Pay>()
                .eq("site_id", siteId)
                .eq("trade_type", tradeType)
                .eq("trade_id", tradeId)
                .ne("status", PayStatusEnum.STATUS_CANCLE.getCode())
        );
    }

    /**
     * 通过交易流水号查询支付单据
     *
     * @param siteId
     * @param outTradeNo
     */
    public Pay findPayInfoByOutTradeNo(Integer siteId, String outTradeNo) {
        return payMapper.selectOne(new QueryWrapper<Pay>()
                .eq("site_id", siteId)
                .eq("out_trade_no", outTradeNo)
        );
    }

    /**
     * 通过业务信息创建支付单据
     *
     * @param siteId
     * @param tradeType
     * @param tradeId
     * @return
     */
    public Pay createByTrade(Integer siteId, String tradeType, Integer tradeId) {
        PayCreateEventDefiner.PayCreateEvent event = new PayCreateEventDefiner.PayCreateEvent();
        event.setSiteId(siteId);
        event.addAppSign("core");
        event.setName("PayCreateEvent");
        event.setTradeType(tradeType);
        event.setTradeId(tradeId);
        List<PayCreateEventDefiner.PayCreateEventResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);

        if (eventData.size() == 0) throw new CommonException("无效的交易类型");
        PayCreateEventDefiner.PayCreateEventResult data = eventData.get(0);

        Pay model = new Pay();
        BeanUtil.copyProperties(data, model);
        model.setSiteId(siteId);
        model.setTradeType(tradeType);
        model.setTradeId(tradeId);
        model.setOutTradeNo(createOutTradeNo());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setChannel(RequestUtils.channel());
        payMapper.insert(model);

        return this.findPayInfoByOutTradeNo(siteId, model.getOutTradeNo());
    }

    /**
     * 创建交易号
     *
     * @return
     */
    private String createOutTradeNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }

    /**
     * 支付成功
     *
     * @param pay
     * @param param
     */
    public void paySuccess(Pay pay, PayNotifyParam param) {
        PaySuccessEvent event = new PaySuccessEvent();
        event.setSiteId(param.getSiteId());
        event.addAppSign("core");
        event.setName("PaySuccessEvent");
        event.setTradeType(pay.getTradeType());
        event.setTradeId(pay.getTradeId());
        event.setPay(pay);
        EventAndSubscribeOfPublisher.publishAll(event);

        BeanUtil.copyProperties(param, pay);
        pay.setStatus(PayStatusEnum.STATUS_FINISH.getCode());
        pay.setPayTime(System.currentTimeMillis() / 1000);

        payMapper.updateById(pay);

        SysPrinterPrintTicketParam printParam = new SysPrinterPrintTicketParam();
        printParam.setSiteId(pay.getSiteId());
        printParam.setType("shopGoodsOrder");
        printParam.setTrigger("pay_after");
        SysPrinterPrintTicketParam.Business business = new SysPrinterPrintTicketParam.Business();
        business.setOrderId(pay.getTradeId());
        printParam.setBusiness(business);
        corePrinterService.printTicket(printParam);
    }

    /**
     * 关闭支付
     *
     * @param pay
     */
    public void payClose(Integer siteId, Pay pay) {
        PayCloseEvent event = new PayCloseEvent();
        event.setSiteId(siteId);
        event.addAppSign("core");
        event.setName("PayCloseEvent");
        event.setTradeType(pay.getTradeType());
        event.setTradeId(pay.getTradeId());
        EventAndSubscribeOfPublisher.publishAll(event);

        pay.setStatus(PayStatusEnum.STATUS_CANCLE.getCode());
        pay.setCancelTime(System.currentTimeMillis() / 1000);
        payMapper.updateById(pay);
    }


    /**
     * 关闭支付
     *
     * @param siteId
     * @param outTradeNo
     */
    public void close(Integer siteId, String outTradeNo) {
        Pay pay = this.findPayInfoByOutTradeNo(siteId, outTradeNo);
        if (pay == null) throw new CommonException("无效的支付交易号");
        if (pay.getStatus().equals(PayStatusEnum.STATUS_CANCLE.getCode())) return;
        if (!pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) && !pay.getStatus().equals(PayStatusEnum.STATUS_WAIT.getCode())) {
            throw new CommonException("只有待支付时可以关闭");
        }

        if (pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) && !pay.getType().isEmpty()) {
            this.driver(siteId, pay.getChannel(), pay.getType()).close(pay);
        }

        this.payClose(siteId, pay);
    }

    /**
     * 关闭支付
     *
     * @param siteId
     * @param pay
     */
    public void close(Integer siteId, Pay pay) {
        if (pay == null) throw new CommonException("无效的支付交易号");
        if (pay.getStatus().equals(PayStatusEnum.STATUS_CANCLE.getCode())) return;
        if (!pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) && !pay.getStatus().equals(PayStatusEnum.STATUS_WAIT.getCode())) {
            throw new CommonException("只有待支付时可以关闭");
        }

        if (pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) && !pay.getType().isEmpty()) {
            this.driver(siteId, pay.getChannel(), pay.getType()).close(pay);
        }

        this.payClose(siteId, pay);
    }

    /**
     * 关闭支付
     *
     * @param siteId
     * @param tradeType
     * @param tradeId
     */
    public void closeByTrade(Integer siteId, String tradeType, Integer tradeId) {
        Pay pay = findPayInfoByTrade(siteId, tradeType, tradeId);
        if (pay == null) return;

        if (pay.getStatus().equals(PayStatusEnum.STATUS_ING.getCode()) || pay.getStatus().equals(PayStatusEnum.STATUS_WAIT.getCode())) {
            this.close(siteId, pay);
        }
    }
}
