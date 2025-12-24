package com.niu.shop.service.api.refund.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.entity.order.ShopOrderRefundLog;
import com.niu.shop.entity.shop_address.ShopAddress;
import com.niu.shop.enums.order.*;
import com.niu.shop.event.refund.AfterShopOrderRefundApplyEvent;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.mapper.order.ShopOrderRefundLogMapper;
import com.niu.shop.mapper.order.ShopOrderRefundMapper;
import com.niu.shop.service.api.refund.IRefundActionService;
import com.niu.shop.service.api.refund.param.ApplyRefundParam;
import com.niu.shop.service.api.refund.param.EditRefundParam;
import com.niu.shop.service.api.refund.param.RefundDeliveryParam;
import com.niu.shop.service.api.refund.param.RefundSearchParam;
import com.niu.shop.service.api.refund.vo.RefundDataVo;
import com.niu.shop.service.api.refund.vo.RefundDetailVo;
import com.niu.shop.service.api.refund.vo.RefundListVo;
import com.niu.shop.service.core.refund.ICoreRefundService;
import com.niu.shop.service.core.refund.param.AddRefundLogParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class RefundActionServiceImpl implements IRefundActionService {

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ShopOrderRefundMapper shopOrderRefundMapper;

    @Resource
    ICoreRefundService coreRefundService;

    @Resource
    ShopOrderRefundLogMapper shopOrderRefundLogMapper;

    /**
     * 订单退款列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return
     */
    @Override
    public PageResult<RefundListVo> page(PageParam pageParam, RefundSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopOrderRefund> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
        queryWrapper.orderByDesc("create_time");

        IPage<ShopOrderRefund> iPage = shopOrderRefundMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<RefundListVo> list = new LinkedList<>();
        for (ShopOrderRefund item : iPage.getRecords()) {
            RefundListVo vo = new RefundListVo();
            BeanUtils.copyProperties(item, vo);

            RefundListVo.OrderGoods orderGoodsVo = new RefundListVo.OrderGoods();
            ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectById(vo.getOrderGoodsId());
            BeanUtil.copyProperties(orderGoods, orderGoodsVo);
            vo.setOrderGoods(orderGoodsVo);

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 详情
     *
     * @param orderRefundNo
     * @return
     */
    @Override
    public RefundDetailVo detail(String orderRefundNo) {
        ShopOrderRefund refund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                .eq("order_refund_no", orderRefundNo)
                .eq("member_id", RequestUtils.memberId())
        );
        if (refund == null) throw new CommonException("退款已失效");

        RefundDetailVo vo = new RefundDetailVo();
        BeanUtil.copyProperties(refund, vo);

        RefundDetailVo.OrderGoods orderGoodsVo = new RefundDetailVo.OrderGoods();
        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectById(vo.getOrderGoodsId());
        BeanUtil.copyProperties(orderGoods, orderGoodsVo);
        vo.setOrderGoods(orderGoodsVo);

        if (!refund.getRefundAddress().isEmpty()) {
            ShopAddress refundAddress = JSONUtil.toBean(JSONUtil.parseObj(refund.getRefundAddress()), ShopAddress.class);
            vo.setRefundAddress(refundAddress);
        }

        List<RefundDetailVo.RefundLog> refundLog = CollectUtils.convert(shopOrderRefundLogMapper.selectList(new QueryWrapper<ShopOrderRefundLog>()
                .eq("order_refund_no", vo.getOrderRefundNo())
                .orderByDesc("create_time")
        ), RefundDetailVo.RefundLog.class);
        vo.setRefundLog(refundLog);

        return vo;
    }

    /**
     * 退款申请
     *
     * @param param
     */
    @Override
    public void apply(ApplyRefundParam param) {
        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectOne(new QueryWrapper<ShopOrderGoods>()
                .eq("order_goods_id", param.getOrderGoodsId())
                .eq("member_id", RequestUtils.memberId()));
        if (orderGoods == null) throw new CommonException("订单已失效");
        if (orderGoods.getIsEnableRefund().equals(0)) throw new CommonException("订单不允许退款");
        if (!orderGoods.getStatus().equals(OrderGoodsRefundEnum.NORMAL.getStatus()))
            throw new CommonException("订单已退款或存在未完成的退款");

        ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>()
                .select("delivery_money")
                .eq("order_id", orderGoods.getOrderId()));
        if (order == null) throw new CommonException("订单已失效");

        if (param.getRefundType().equals(OrderRefundTypeEnum.RETURN_AND_REFUND_GOODS) && orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.WAIT_DELIVERY))
            throw new CommonException("只有已发货的商品才能申请退货退款");

        Integer isRefundDelivery = 0;
        // 订单项数量
        Long orderGoodsCount = shopOrderGoodsMapper.selectCount(new QueryWrapper<ShopOrderGoods>().eq("order_id", orderGoods.getOrderId()).eq("is_gift", 0));
        // 已申请退款的订单项数量
        Long refundCount = shopOrderRefundMapper.selectCount(new QueryWrapper<ShopOrderRefund>().eq("order_id", orderGoods.getOrderId()).ne("status", OrderRefundStatusEnum.CLOSE.getStatus()));

        if (orderGoodsCount.equals(refundCount + 1)) {
            Long refundDeliveryCount = shopOrderRefundMapper.selectCount(new QueryWrapper<ShopOrderRefund>().eq("order_id", orderGoods.getOrderId()).eq("is_refund_delivery", 1).ne("status", OrderRefundStatusEnum.CLOSE.getStatus()));
            if (refundDeliveryCount == 0) isRefundDelivery = 1;
        }

        BigDecimal maxRefundMoney = orderGoods.getGoodsMoney().subtract(orderGoods.getDiscountMoney());
        if (isRefundDelivery.equals(1)) maxRefundMoney = maxRefundMoney.add(order.getDeliveryMoney());

        if (param.getApplyMoney().compareTo(maxRefundMoney) == 1)
            throw new CommonException("退款金额不能大于可退款总额");

        ShopOrderRefund model = new ShopOrderRefund();
        BeanUtil.copyProperties(param, model);
        model.setOrderRefundNo(createRefundNo());
        model.setSiteId(orderGoods.getSiteId());
        model.setMemberId(RequestUtils.memberId());
        model.setStatus(OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus().toString());
        model.setSource(OrderRefundSourceEnum.APPLY.getType().toString());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setVoucher(JSONUtil.parseArray(param.getVoucher()).toString());
        model.setIsRefundDelivery(isRefundDelivery);
        shopOrderRefundMapper.insert(model);

        ShopOrderGoods orderGoodsUpdate = new ShopOrderGoods();
        orderGoodsUpdate.setOrderGoodsId(orderGoods.getOrderGoodsId());
        orderGoodsUpdate.setOrderRefundNo(model.getOrderRefundNo());
        orderGoodsUpdate.setStatus(OrderGoodsRefundEnum.REFUNDING.getStatus());
        shopOrderGoodsMapper.updateById(orderGoodsUpdate);

        // 添加日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(model.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        logParam.setMainId(RequestUtils.memberId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setType(OrderRefundActionTypeEnum.APPLY_ACTION.getType());
        coreRefundService.addLog(logParam);

        AfterShopOrderRefundApplyEvent sendApplyEvent = new AfterShopOrderRefundApplyEvent();
        sendApplyEvent.setOrderRefundNo(model.getOrderRefundNo());
        sendApplyEvent.setOrderRefund(model);
        EventAndSubscribeOfPublisher.publishAll(sendApplyEvent);
    }

    /**
     * 修改退款申请
     *
     * @param param
     */
    @Override
    public void edit(EditRefundParam param) {
        ShopOrderRefund refund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
                .eq("member_id", RequestUtils.memberId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus().toString())
                && !refund.getStatus().equals(OrderRefundStatusEnum.STORE_REFUSE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus().toString())) {
            throw new CommonException("退款已失效");
        }

        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectOne(new QueryWrapper<ShopOrderGoods>()
                .eq("order_goods_id", refund.getOrderGoodsId()));
        if (orderGoods == null) throw new CommonException("订单已失效");

        ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>()
                .select("delivery_money")
                .eq("order_id", orderGoods.getOrderId()));
        if (order == null) throw new CommonException("订单已失效");

        BigDecimal maxRefundMoney = orderGoods.getGoodsMoney().subtract(orderGoods.getDiscountMoney());
        if (refund.getIsRefundDelivery().equals(1)) maxRefundMoney = maxRefundMoney.add(order.getDeliveryMoney());

        if (param.getApplyMoney().compareTo(maxRefundMoney) == 1)
            throw new CommonException("退款金额不能大于可退款总额");

        ShopOrderRefund model = new ShopOrderRefund();
        BeanUtil.copyProperties(param, model);
        model.setRefundId(refund.getRefundId());
        model.setShopReason("");
        model.setVoucher(JSONUtil.parseArray(param.getVoucher()).toString());
        model.setStatus(OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus().toString());
        shopOrderRefundMapper.updateById(model);

        // 添加日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        logParam.setMainId(RequestUtils.memberId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setType(OrderRefundActionTypeEnum.EDIT_APPLY_ACTION.getType());
        coreRefundService.addLog(logParam);
    }

    /**
     * 买家退货
     *
     * @param param
     */
    @Override
    public void delivery(RefundDeliveryParam param) {
        ShopOrderRefund refund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
                .eq("member_id", RequestUtils.memberId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.STORE_AGREE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus().toString())) {
            throw new CommonException("退款已失效");
        }

        ShopOrderRefund model = new ShopOrderRefund();
        model.setRefundId(refund.getRefundId());
        model.setDelivery(JSONUtil.parse(param.getDelivery()).toString());
        model.setStatus(OrderRefundStatusEnum.BUYER_REFUND_GOODS_WAIT_STORE.getStatus().toString());
        shopOrderRefundMapper.updateById(model);

        // 添加日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        logParam.setMainId(RequestUtils.memberId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setType(OrderRefundActionTypeEnum.DELIVERY_ACTION.getType());
        coreRefundService.addLog(logParam);
    }

    /**
     * 修改买家退货
     *
     * @param param
     */
    @Override
    public void editDelivery(RefundDeliveryParam param) {
        ShopOrderRefund refund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
                .eq("member_id", RequestUtils.memberId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.STORE_REFUSE_TAKE_REFUND_GOODS_WAIT_BUYER.getStatus().toString())
                && !refund.getStatus().equals(OrderRefundStatusEnum.BUYER_REFUND_GOODS_WAIT_STORE.getStatus().toString())) {
            throw new CommonException("退款已失效");
        }

        ShopOrderRefund model = new ShopOrderRefund();
        model.setRefundId(refund.getRefundId());
        model.setDelivery(JSONUtil.parse(param.getDelivery()).toString());
        model.setStatus(OrderRefundStatusEnum.BUYER_REFUND_GOODS_WAIT_STORE.getStatus().toString());
        shopOrderRefundMapper.updateById(model);

        // 添加日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        logParam.setMainId(RequestUtils.memberId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setType(OrderRefundActionTypeEnum.EDIT_DELIVERY_ACTION.getType());
        coreRefundService.addLog(logParam);
    }

    /**
     * 创建交易号
     *
     * @return
     */
    private String createRefundNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }

    /**
     * 查询订单项退款信息
     *
     * @param orderGoodsId
     * @return
     */
    @Override
    public RefundDataVo getRefundData(Integer orderGoodsId) {
        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectOne(new QueryWrapper<ShopOrderGoods>()
                .select("order_id,order_goods_money")
                .eq("order_goods_id", orderGoodsId)
                .eq("member_id", RequestUtils.memberId()));
        if (orderGoods == null) throw new CommonException("订单已失效");

        ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>()
                .select("delivery_money")
                .eq("order_id", orderGoods.getOrderId()));
        if (order == null) throw new CommonException("订单已失效");

        Integer isRefundDelivery = 0;

        // 订单项数量
        Long orderGoodsCount = shopOrderGoodsMapper.selectCount(new QueryWrapper<ShopOrderGoods>().eq("order_id", orderGoods.getOrderId()).eq("is_gift", 0));
        // 已申请退款的订单项数量
        Long refundCount = shopOrderRefundMapper.selectCount(new QueryWrapper<ShopOrderRefund>().eq("order_id", orderGoods.getOrderId()).ne("status", OrderRefundStatusEnum.CLOSE.getStatus()));

        if (orderGoodsCount.equals(refundCount + 1)) {
            Long refundDeliveryCount = shopOrderRefundMapper.selectCount(new QueryWrapper<ShopOrderRefund>().eq("order_id", orderGoods.getOrderId()).eq("is_refund_delivery", 1).ne("status", OrderRefundStatusEnum.CLOSE.getStatus()));
            if (refundDeliveryCount == 0) isRefundDelivery = 1;
        }

        RefundDataVo vo = new RefundDataVo();
        vo.setRefundMoney(orderGoods.getOrderGoodsMoney());
        vo.setRefundOrderGoodsMoney(orderGoods.getOrderGoodsMoney());
        vo.setIsRefundDelivery(isRefundDelivery);

        if (isRefundDelivery.equals(1)) {
            vo.setRefundMoney(vo.getRefundMoney().add(order.getDeliveryMoney()));
            vo.setRefundDeliveryMoney(order.getDeliveryMoney());
        }

        return vo;
    }

    /**
     * 查询订单项退款信息
     *
     * @param orderRefundNo
     * @return
     */
    @Override
    public RefundDataVo getRefundData(String orderRefundNo) {
        ShopOrderRefund refund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                .eq("order_refund_no", orderRefundNo)
                .eq("member_id", RequestUtils.memberId()));
        if (refund == null) throw new CommonException("退款已失效");

        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectOne(new QueryWrapper<ShopOrderGoods>()
                .select("order_id,order_goods_money")
                .eq("order_goods_id", refund.getOrderGoodsId())
                .eq("member_id", RequestUtils.memberId()));
        if (orderGoods == null) throw new CommonException("订单已失效");

        ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>()
                .select("delivery_money")
                .eq("order_id", orderGoods.getOrderId()));
        if (order == null) throw new CommonException("订单已失效");

        RefundDataVo vo = new RefundDataVo();
        vo.setRefundMoney(orderGoods.getOrderGoodsMoney());
        vo.setRefundOrderGoodsMoney(orderGoods.getOrderGoodsMoney());
        vo.setIsRefundDelivery(refund.getIsRefundDelivery());
        if (refund.getIsRefundDelivery().equals(1)) {
            vo.setRefundMoney(vo.getRefundMoney().add(order.getDeliveryMoney()));
            vo.setRefundDeliveryMoney(order.getDeliveryMoney());
        }

        return vo;
    }

    /**
     * 取消退款
     *
     * @param orderRefundNo
     */
    @Override
    public void close(String orderRefundNo) {
        ShopOrderRefund refund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                .eq("order_refund_no", orderRefundNo)
                .eq("member_id", RequestUtils.memberId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        String[] status = {
                OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString(),
                OrderRefundStatusEnum.STORE_REFUND_TRANSFERING.getStatus().toString(),
                OrderRefundStatusEnum.FINISH.getStatus().toString(),
                OrderRefundStatusEnum.CLOSE.getStatus().toString()
        };
        if (Arrays.asList(status).contains(refund.getStatus())) {
            throw new CommonException("退款已失效(退款已完成或已关闭)");
        }

        ShopOrderRefund model = new ShopOrderRefund();
        model.setRefundId(refund.getRefundId());
        model.setStatus(OrderRefundStatusEnum.CLOSE.getStatus().toString());
        shopOrderRefundMapper.updateById(model);

        ShopOrderGoods orderGoods = new ShopOrderGoods();
        orderGoods.setStatus(OrderGoodsRefundEnum.NORMAL.getStatus());
        shopOrderGoodsMapper.update(orderGoods, new QueryWrapper<ShopOrderGoods>().eq("order_refund_no", refund.getOrderRefundNo()));

        // 添加日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        logParam.setMainId(RequestUtils.memberId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setType(OrderRefundActionTypeEnum.CLOSE_ACTION.getType());
        coreRefundService.addLog(logParam);
    }
}
