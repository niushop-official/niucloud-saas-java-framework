package com.niu.shop.service.admin.refund.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.NoticeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.enums.pay.RefundTypeEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayRefundMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.core.pay.param.PayRefundCreateParam;
import com.niu.core.service.core.pay.param.PayRefundTransferParam;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.entity.order.ShopOrderRefundLog;
import com.niu.shop.entity.shop_address.ShopAddress;
import com.niu.shop.enums.order.*;
import com.niu.shop.mapper.delivery.ShopStoreMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.mapper.order.ShopOrderRefundLogMapper;
import com.niu.shop.mapper.order.ShopOrderRefundMapper;
import com.niu.shop.mapper.shop_address.ShopAddressMapper;
import com.niu.shop.service.admin.order.vo.ShopOrderInfoVo;
import com.niu.shop.service.admin.refund.IRefundActionService;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.refund.ICoreRefundActionService;
import com.niu.shop.service.core.refund.param.ActiveRefundCloseParam;
import com.niu.shop.service.core.refund.param.ActiveRefundParam;
import com.niu.shop.service.admin.refund.param.AuditApplyParam;
import com.niu.shop.service.admin.refund.param.AuditRefundGoodsParam;
import com.niu.shop.service.admin.refund.param.RefundSearchParam;
import com.niu.shop.service.admin.refund.vo.RefundDetailVo;
import com.niu.shop.service.admin.refund.vo.RefundListVo;
import com.niu.shop.service.core.refund.ICoreRefundService;
import com.niu.shop.service.core.refund.param.AddRefundLogParam;
import com.niu.shop.service.core.refund.param.RefundFinishParam;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    @Resource
    MemberMapper memberMapper;

    @Resource
    PayRefundMapper payRefundMapper;

    @Resource
    ShopAddressMapper shopAddressMapper;

    @Resource
    com.niu.core.service.core.pay.ICoreRefundService corePayRefundService;

    @Resource
    ICoreRefundActionService coreRefundActionService;

    @Resource
    ShopStoreMapper shopStoreMapper;

    @Resource
    ICoreManjianService coreManjianService;

    /**
     * 订单退款列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return
     */
    @Override
    public PageResult<RefundListVo> page(PageParam pageParam, RefundSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopOrderRefund> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderRefundNo())) queryWrapper.like("order_refund_no", searchParam.getOrderRefundNo());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
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

            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", vo.getMemberId()));
            BeanUtil.copyProperties(member, memberVo);
            vo.setMember(memberVo);

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 详情
     *
     * @param refundId
     * @return
     */
    @Override
    public RefundDetailVo detail(Integer refundId) {
        ShopOrderRefund refund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                .eq("refund_id", refundId)
                .eq("site_id", RequestUtils.siteId())
        );
        if (refund == null) throw new CommonException("退款已失效");

        RefundDetailVo vo = new RefundDetailVo();
        BeanUtil.copyProperties(refund, vo);

        RefundDetailVo.OrderGoods orderGoodsVo = new RefundDetailVo.OrderGoods();
        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectById(vo.getOrderGoodsId());
        BeanUtil.copyProperties(orderGoods, orderGoodsVo);
        vo.setOrderGoods(orderGoodsVo);

        List<RefundDetailVo.RefundLog> refundLog = CollectUtils.convert(shopOrderRefundLogMapper.selectList(new QueryWrapper<ShopOrderRefundLog>()
                .eq("order_refund_no", vo.getOrderRefundNo())
                .orderByDesc("create_time")
        ), RefundDetailVo.RefundLog.class);
        vo.setRefundLog(refundLog);

        MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", vo.getMemberId()));
        BeanUtil.copyProperties(member, memberVo);
        vo.setMember(memberVo);

        RefundDetailVo.OrderMain orderVo = new RefundDetailVo.OrderMain();
        ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>().eq("order_id", vo.getOrderId()));
        BeanUtil.copyProperties(order, orderVo);
        vo.setOrderMain(orderVo);

        if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType())) {
            ShopStore store = shopStoreMapper.selectOne(new QueryWrapper<ShopStore>().eq("store_id", order.getTakeStoreId()));
            if (store != null) {
                RefundDetailVo.Store storeVo = new RefundDetailVo.Store();
                BeanUtil.copyProperties(store, storeVo);
                vo.setStore(storeVo);
            }
        }

        if (ObjectUtil.isNotEmpty(vo.getRefundNo())) {
            RefundDetailVo.PayRefund payRefundVo = new RefundDetailVo.PayRefund();
            PayRefund payRefund = payRefundMapper.selectOne(new QueryWrapper<PayRefund>().eq("refund_no", vo.getRefundNo()));
            BeanUtil.copyProperties(payRefund, payRefundVo);
            vo.setPayRefund(payRefundVo);
        }

        if (!refund.getDelivery().isEmpty()) {
            RefundDetailVo.Delivery delivery = JSONUtil.toBean(JSONUtil.parseObj(refund.getDelivery()), RefundDetailVo.Delivery.class);
            vo.setDelivery(delivery);
        }

        Map<Integer, JSONObject> refundGiftList = coreManjianService.refundCheck(refund);
        if (ObjectUtil.isNotEmpty(refundGiftList)) {
            refundGiftList.values().stream().forEach(v -> {
                vo.setGiftBalance(vo.getGiftBalance().add(v.getBigDecimal("balance", BigDecimal.ZERO)));
            });
        }

        return vo;
    }

    /**
     * 订单维权审核
     *
     * @param param
     */
    @Override
    public void auditApply(AuditApplyParam param) {
        ShopOrderRefund refund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
                .eq("site_id", RequestUtils.siteId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus().toString())) {
            throw new CommonException("退款已失效(只有申请中的退款才可以审核)");
        }

        ShopOrderRefund model = new ShopOrderRefund();
        model.setRefundId(refund.getRefundId());

        // 操作日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        logParam.setMainId(RequestUtils.uid());

        String messageKey = "";

        // 同意退款
        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectOne(new QueryWrapper<ShopOrderGoods>()
                .select("goods_money,discount_money")
                .eq("order_goods_id", refund.getOrderGoodsId()));
        if (orderGoods == null) throw new CommonException("订单已失效");

        ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>()
                .select("order_no,delivery_money")
                .eq("order_id", refund.getOrderId()));
        if (order == null) throw new CommonException("订单已失效");

        if (param.getIsAgree().equals(1)) {
            BigDecimal maxRefundMoney = orderGoods.getGoodsMoney().subtract(orderGoods.getDiscountMoney());
            if (refund.getIsRefundDelivery().equals(1)) maxRefundMoney = maxRefundMoney.add(order.getDeliveryMoney());

            if (param.getMoney().compareTo(maxRefundMoney) == 1) throw new CommonException("退款金额不能大于可退款总额");

            model.setMoney(param.getMoney());
            // 如果是退款退货
            if (refund.getRefundType().equals(OrderRefundTypeEnum.RETURN_AND_REFUND_GOODS.getType().toString())) {
                model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus().toString());
                ShopAddress address = shopAddressMapper.selectOne(new QueryWrapper<ShopAddress>()
                        .eq("site_id", RequestUtils.siteId())
                        .eq("id", param.getRefundAddressId())
                );
                if (address == null) throw new CommonException("请选择退货地址");
                model.setRefundAddress(JSONUtil.parseObj(address).toString());
            } else {
                model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString());
            }

            logParam.setType(OrderRefundActionTypeEnum.AGREE_AUDIT_APPLY_ACTION.getType());

            messageKey = "shop_refund_agree";
        } else {
            // 拒绝退款
            model.setShopReason(param.getShopReason());
            model.setStatus(OrderRefundStatusEnum.STORE_REFUSE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus().toString());
            logParam.setType(OrderRefundActionTypeEnum.REFUSE_AUDIT_APPLY_ACTION.getType());

            messageKey = "shop_refund_refuse";
        }

        shopOrderRefundMapper.updateById(model);

        logParam.setStatus(Integer.valueOf(model.getStatus()));
        coreRefundService.addLog(logParam);

        if (model.getStatus().equals(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString())) {
            refund = shopOrderRefundMapper.selectById(refund.getRefundId());
            coreRefundService.toTransfer(refund);
        }

        // 发送消息
        Map<String, Object> noticeParam = new HashMap<String, Object>();
        noticeParam.put("refund", refund);
        noticeParam.put("order", order);
        NoticeUtils.send(refund.getSiteId(), messageKey, noticeParam);
    }

    /**
     * 订单退货审核
     *
     * @param param
     */
    @Override
    public void auditRefundGoods(AuditRefundGoodsParam param) {
        ShopOrderRefund refund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
                .eq("site_id", RequestUtils.siteId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.BUYER_REFUND_GOODS_WAIT_STORE.getStatus().toString())) {
            throw new CommonException("退款已失效(只有待确认收货的的退款才可以审核)");
        }

        ShopOrderRefund model = new ShopOrderRefund();
        model.setRefundId(refund.getRefundId());

        // 操作日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        logParam.setMainId(RequestUtils.uid());

        if (param.getIsAgree().equals(1)) {
            model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString());
            logParam.setType(OrderRefundActionTypeEnum.AGREE_AUDIT_REFUND_GOODS_ACTION.getType());
        } else {
            // 拒绝退货
            model.setShopReason(param.getShopReason());
            model.setStatus(OrderRefundStatusEnum.STORE_REFUSE_TAKE_REFUND_GOODS_WAIT_BUYER.getStatus().toString());
            logParam.setType(OrderRefundActionTypeEnum.REFUSE_AUDIT_REFUND_GOODS_ACTION.getType());
        }

        shopOrderRefundMapper.updateById(model);

        logParam.setStatus(Integer.valueOf(model.getStatus()));
        coreRefundService.addLog(logParam);

        if (model.getStatus().equals(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString())) {
            refund = shopOrderRefundMapper.selectById(refund.getRefundId());
            coreRefundService.toTransfer(refund);
        }
    }

    /**
     * 商家主动退款
     * @param param
     */
    @Override
    public void shopActiveRefund(ActiveRefundParam param) {
        param.setMainType(OrderLogMainTypeEnum.STORE.getType());
        param.setMainId(RequestUtils.uid());
        param.setSiteId(RequestUtils.siteId());
        coreRefundActionService.shopActiveRefund(param);
    }

    @Override
    public void close(String orderRefundNo) {
        coreRefundActionService.close(new ActiveRefundCloseParam().builder()
                .orderRefundNo(orderRefundNo)
                .mainId(RequestUtils.uid())
                .mainType(OrderLogMainTypeEnum.STORE.getType())
                .siteId(RequestUtils.siteId())
                .build(), OrderRefundStatusEnum.SHOP_ACTIVE_CLOSE_REFUND.getStatus());
    }
}
