package com.niu.recharge.service.admin.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.recharge.entity.RechargeOrder;
import com.niu.recharge.entity.RechargeOrderItem;
import com.niu.recharge.enums.OrderStatusEnum;
import com.niu.recharge.enums.RefundStatusEnum;
import com.niu.recharge.mapper.RechargeOrderItemMapper;
import com.niu.recharge.mapper.RechargeOrderMapper;
import com.niu.recharge.service.admin.IRechargeOrderService;
import com.niu.recharge.service.admin.param.RechargeOrderSearchParam;
import com.niu.recharge.service.admin.vo.RechargeOrderInfoVo;
import com.niu.recharge.service.admin.vo.RechargeOrderListVo;
import com.niu.recharge.service.admin.vo.RechargeOrderStatVo;
import com.niu.recharge.service.core.ICoreRechargeOrderService;
import com.niu.recharge.service.core.param.RechargeOrderCreateParam;
import com.niu.recharge.service.core.vo.RechargeOrderCreateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
public class RechargeOrderServiceImpl implements IRechargeOrderService {

    @Resource
    ICoreRechargeOrderService coreRechargeOrderService;

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    RechargeOrderItemMapper rechargeOrderItemMapper;

    @Resource
    MemberMapper memberMapper;

    /**
     * 订单列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public PageResult<RechargeOrderListVo> list(PageParam pageParam, RechargeOrderSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<RechargeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getOrderNo())) queryWrapper.eq("order_no", searchParam.getOrderNo());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderStatus())) queryWrapper.eq("order_status", searchParam.getOrderStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderFrom())) queryWrapper.eq("order_from", searchParam.getOrderFrom());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        if (ObjectUtil.isNotEmpty(searchParam.getPayTime())) QueryMapperUtils.buildByTime(queryWrapper, "pay_time", searchParam.getPayTime());
        if (searchParam.getStartMoney() > 0) queryWrapper.ge("order_money", searchParam.getStartMoney());
        if (searchParam.getEndMoney() > 0) queryWrapper.le("order_money", searchParam.getEndMoney());

        IPage<RechargeOrder> iPage = rechargeOrderMapper.selectPage(new Page<RechargeOrder>(page, limit), queryWrapper);
        List<RechargeOrderListVo> list = new LinkedList<>();
        for(RechargeOrder item : iPage.getRecords()) {
            RechargeOrderListVo vo = new RechargeOrderListVo();
            BeanUtils.copyProperties(item, vo);
            MemberBriefInfoVo memberInfo = new MemberBriefInfoVo();
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", item.getMemberId()));
            if (member != null) BeanUtils.copyProperties(member, memberInfo);
            vo.setMember(memberInfo);
            List<RechargeOrderItem> itemList = rechargeOrderItemMapper.selectList(new QueryWrapper<RechargeOrderItem>().eq("order_id", item.getOrderId()));
            vo.setItem(itemList);
            list.add(vo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 充值订单详情
     *
     * @param orderId
     * @return
     */
    @Override
    public RechargeOrderInfoVo detail(Integer orderId) {
        QueryWrapper<RechargeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        queryWrapper.eq("site_id", RequestUtils.siteId());

        RechargeOrder order = rechargeOrderMapper.selectOne(queryWrapper);
        if (order == null) return null;

        RechargeOrderInfoVo vo = new RechargeOrderInfoVo();
        BeanUtils.copyProperties(order, vo);

        MemberBriefInfoVo memberInfo = new MemberBriefInfoVo();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", order.getMemberId()));
        if (member != null) BeanUtils.copyProperties(member, memberInfo);
        vo.setMember(memberInfo);

        List<RechargeOrderItem> itemList = rechargeOrderItemMapper.selectList(new QueryWrapper<RechargeOrderItem>().eq("order_id", order.getOrderId()));
        vo.setItem(itemList);

        return vo;
    }

    /**
     * 统计
     *
     * @param searchParam
     * @return
     */
    public RechargeOrderStatVo stat(RechargeOrderSearchParam searchParam) {
        RechargeOrderStatVo vo = new RechargeOrderStatVo();

        QueryWrapper rechargeMoneyQueryWrapper = new QueryWrapper<RechargeOrder>();
        rechargeMoneyQueryWrapper.select("SUM(order_money) AS order_money");
        rechargeMoneyQueryWrapper.eq("site_id", RequestUtils.siteId());
        rechargeMoneyQueryWrapper.eq("order_status", OrderStatusEnum.FINISH.getStatus());
        if (searchParam.getMemberId() > 0) rechargeMoneyQueryWrapper.eq("member_id", searchParam.getMemberId());
        rechargeMoneyQueryWrapper.last("limit 1");
        RechargeOrder rechargeMoney = rechargeOrderMapper.selectOne(rechargeMoneyQueryWrapper);
        if (rechargeMoney != null) vo.setRechargeMoney(rechargeMoney.getOrderMoney());

        QueryWrapper refundMoneyQueryWrapper = new QueryWrapper<RechargeOrder>();
        refundMoneyQueryWrapper.select("SUM(order_money) AS order_money");
        refundMoneyQueryWrapper.eq("site_id", RequestUtils.siteId());
        refundMoneyQueryWrapper.eq("refund_status", RefundStatusEnum.REFUND_COMPLETED.getStatus());
        if (searchParam.getMemberId() > 0) refundMoneyQueryWrapper.eq("member_id", searchParam.getMemberId());
        refundMoneyQueryWrapper.last("limit 1");
        RechargeOrder refundeMoney = rechargeOrderMapper.selectOne(refundMoneyQueryWrapper);
        if (refundeMoney != null) vo.setRechargeRefundMoney(refundeMoney.getOrderMoney());

        return vo;
    }
}
