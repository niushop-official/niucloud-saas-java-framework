package com.niu.recharge.service.api.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.recharge.entity.RechargeOrder;
import com.niu.recharge.entity.RechargeOrderItem;
import com.niu.recharge.mapper.RechargeOrderItemMapper;
import com.niu.recharge.mapper.RechargeOrderMapper;
import com.niu.recharge.service.api.IRechargeOrderService;
import com.niu.recharge.service.api.param.RechargeOrderSearchParam;
import com.niu.recharge.service.api.vo.RechargeOrderInfoVo;
import com.niu.recharge.service.api.vo.RechargeOrderListVo;
import com.niu.recharge.service.core.ICoreRechargeOrderService;
import com.niu.recharge.service.core.param.RechargeOrderCreateParam;
import com.niu.recharge.service.core.vo.RechargeOrderCreateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    /**
     * 创建订单
     *
     * @param param
     * @return
     */
    @Override
    public RechargeOrderCreateVo create(RechargeOrderCreateParam param) {
        param.setMemberId(RequestUtils.memberId());
        param.setSiteId(RequestUtils.siteId());
        return coreRechargeOrderService.create(param);
    }

    /**
     * 订单列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public PageResult<RechargeOrderListVo> list(PageParam pageParam, RechargeOrderSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<RechargeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getOrderStatus()))
            queryWrapper.like("order_status", searchParam.getOrderStatus());

        IPage<RechargeOrder> iPage = rechargeOrderMapper.selectPage(new Page<RechargeOrder>(page, limit), queryWrapper);
        List<RechargeOrderListVo> list = new LinkedList<>();
        for (RechargeOrder item : iPage.getRecords()) {
            RechargeOrderListVo vo = new RechargeOrderListVo();
            BeanUtils.copyProperties(item, vo);
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
        queryWrapper.eq("member_id", RequestUtils.memberId());

        RechargeOrder order = rechargeOrderMapper.selectOne(queryWrapper);
        if (order == null) return null;

        RechargeOrderInfoVo vo = new RechargeOrderInfoVo();
        BeanUtils.copyProperties(order, vo);

        List<RechargeOrderItem> itemList = rechargeOrderItemMapper.selectList(new QueryWrapper<RechargeOrderItem>().eq("order_id", order.getOrderId()));
        vo.setItem(itemList);

        return vo;
    }


}
