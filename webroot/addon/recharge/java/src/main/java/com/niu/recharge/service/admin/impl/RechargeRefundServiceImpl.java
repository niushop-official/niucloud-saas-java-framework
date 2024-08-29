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
import com.niu.recharge.entity.RechargeOrderItem;
import com.niu.recharge.entity.RechargeOrderItemRefund;
import com.niu.recharge.enums.RefundStatusEnum;
import com.niu.recharge.mapper.RechargeOrderItemMapper;
import com.niu.recharge.mapper.RechargeOrderItemRefundMapper;
import com.niu.recharge.service.admin.IRechargeRefundService;
import com.niu.recharge.service.admin.param.RechargeRefundSearchParam;
import com.niu.recharge.service.admin.vo.*;
import com.niu.recharge.service.core.ICoreRechargeRefundService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
public class RechargeRefundServiceImpl implements IRechargeRefundService {

    @Resource
    RechargeOrderItemRefundMapper rechargeOrderItemRefundMapper;

    @Resource
    RechargeOrderItemMapper rechargeOrderItemMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreRechargeRefundService coreRechargeRefundService;

    /**
     * 订单列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public PageResult<RechargeRefundListVo> list(PageParam pageParam, RechargeRefundSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<RechargeOrderItemRefund> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getOrderNo())) queryWrapper.eq("order_no", searchParam.getOrderNo());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderStatus())) queryWrapper.eq("order_status", searchParam.getOrderStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderFrom())) queryWrapper.eq("order_from", searchParam.getOrderFrom());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        if (ObjectUtil.isNotEmpty(searchParam.getPayTime())) QueryMapperUtils.buildByTime(queryWrapper, "pay_time", searchParam.getPayTime());
        if (searchParam.getStartMoney() > 0) queryWrapper.ge("order_money", searchParam.getStartMoney());
        if (searchParam.getEndMoney() > 0) queryWrapper.le("order_money", searchParam.getEndMoney());

        IPage<RechargeOrderItemRefund> iPage = rechargeOrderItemRefundMapper.selectPage(new Page<RechargeOrderItemRefund>(page, limit), queryWrapper);
        List<RechargeRefundListVo> list = new LinkedList<>();
        for(RechargeOrderItemRefund item : iPage.getRecords()) {
            RechargeRefundListVo vo = new RechargeRefundListVo();
            BeanUtils.copyProperties(item, vo);
            MemberBriefInfoVo memberInfo = new MemberBriefInfoVo();
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", item.getMemberId()));
            if (member != null) BeanUtils.copyProperties(member, memberInfo);
            vo.setMember(memberInfo);
            RechargeOrderItem orderItem = rechargeOrderItemMapper.selectOne(new QueryWrapper<RechargeOrderItem>().eq("order_item_id", item.getOrderItemId()));
            vo.setItem(orderItem);
            list.add(vo);
        }

        return PageResult.build(page,limit).setTotal(iPage.getTotal()).setData(list);
    }

    /**
     * 发起退款
     *
     */
    public void refund(Integer orderId) {
        coreRechargeRefundService.create(orderId);
    }

    /**
     * 充值订单详情
     *
     * @param refundId
     * @return
     */
    @Override
    public RechargeRefundInfoVo detail(Integer refundId) {
        QueryWrapper<RechargeOrderItemRefund> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("refund_id", refundId);
        queryWrapper.eq("site_id", RequestUtils.siteId());

        RechargeOrderItemRefund refund = rechargeOrderItemRefundMapper.selectOne(queryWrapper);
        if (refund == null) return null;

        RechargeRefundInfoVo vo = new RechargeRefundInfoVo();
        BeanUtils.copyProperties(refund, vo);

        MemberBriefInfoVo memberInfo = new MemberBriefInfoVo();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", refund.getMemberId()));
        if (member != null) BeanUtils.copyProperties(member, memberInfo);
        vo.setMember(memberInfo);

        RechargeOrderItem item = rechargeOrderItemMapper.selectOne(new QueryWrapper<RechargeOrderItem>().eq("order_item_id", refund.getOrderItemId()));
        vo.setItem(item);

        return vo;
    }

    /**
     * 统计
     *
     * @param searchParam
     * @return
     */
    public RechargeRefundStatVo stat(RechargeRefundSearchParam searchParam) {
        RechargeRefundStatVo vo = new RechargeRefundStatVo();

        QueryWrapper allMoneyQueryWrapper = new QueryWrapper<RechargeOrderItemRefund>();
        allMoneyQueryWrapper.select("SUM(money) AS money");
        allMoneyQueryWrapper.eq("site_id", RequestUtils.siteId());
        allMoneyQueryWrapper.ne("status", RefundStatusEnum.REFUND_FAIL.getStatus());
        if (searchParam.getMemberId() > 0) allMoneyQueryWrapper.eq("member_id", searchParam.getMemberId());
        allMoneyQueryWrapper.last("limit 1");
        RechargeOrderItemRefund allMoney = rechargeOrderItemRefundMapper.selectOne(allMoneyQueryWrapper);

        RechargeRefundStatVo.Stat all = new RechargeRefundStatVo.Stat();
        all.setKey("all");
        all.setName("累计退款金额（元）");
        if (allMoney != null)all.setMoney(allMoney.getMoney());
        vo.setAll(all);

        QueryWrapper haveMoneyQueryWrapper = new QueryWrapper<RechargeOrderItemRefund>();
        haveMoneyQueryWrapper.select("SUM(money) AS money");
        haveMoneyQueryWrapper.eq("site_id", RequestUtils.siteId());
        haveMoneyQueryWrapper.eq("status", RefundStatusEnum.REFUNDING.getStatus());
        if (searchParam.getMemberId() > 0) haveMoneyQueryWrapper.eq("member_id", searchParam.getMemberId());
        haveMoneyQueryWrapper.last("limit 1");
        RechargeOrderItemRefund haveMoney = rechargeOrderItemRefundMapper.selectOne(haveMoneyQueryWrapper);

        RechargeRefundStatVo.Stat have = new RechargeRefundStatVo.Stat();
        have.setKey("have");
        have.setName("退款中金额（元）");
        if (haveMoney != null) have.setMoney(haveMoney.getMoney());
        vo.setHave(have);

        return vo;
    }
}
