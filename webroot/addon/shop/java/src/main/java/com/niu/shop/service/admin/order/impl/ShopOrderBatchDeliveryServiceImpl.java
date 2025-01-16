package com.niu.shop.service.admin.order.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.shop.entity.order.ShopOrderBatchDelivery;
import com.niu.shop.enums.order.OrderBatchDeliveryStatusEnum;
import com.niu.shop.enums.order.OrderBatchDeliveryTypeEnum;
import com.niu.shop.mapper.order.ShopOrderBatchDeliveryMapper;
import com.niu.shop.service.admin.order.IShopOrderBatchDeliveryService;
import com.niu.shop.service.admin.order.param.ShopOrderAddBatchDeliveryParam;
import com.niu.shop.service.admin.order.param.ShopOrderBatchDeliverySearchParam;
import com.niu.shop.service.admin.order.vo.ShopOrderBatchDeliveryInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderBatchDeliveryListVo;
import com.niu.shop.service.core.order.ICoreOrderBatchDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 订单批量发货实现
 */
@Service
@RequiredArgsConstructor
public class ShopOrderBatchDeliveryServiceImpl implements IShopOrderBatchDeliveryService {
    private final SysUserMapper sysUserMapper;
    private final ShopOrderBatchDeliveryMapper shopOrderBatchDeliveryMapper;
    private final ICoreOrderBatchDeliveryService coreOrderBatchDeliveryService;

    @Override
    public PageResult<ShopOrderBatchDeliveryListVo> list(PageParam pageParam, ShopOrderBatchDeliverySearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopOrderBatchDelivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("status", searchParam.getStatus());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getType())) {
            queryWrapper.eq("type", searchParam.getType());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getMainId())) {
            queryWrapper.eq("main_id", searchParam.getMainId());
        }

        IPage<ShopOrderBatchDelivery> iPage = shopOrderBatchDeliveryMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<Integer> uIds = CollStreamUtil.toList(iPage.getRecords(), ShopOrderBatchDelivery::getMainId);
        Map<Integer, SysUser> userMap = Collections.emptyMap();
        if (ObjectUtil.isNotEmpty(uIds)) {
            userMap = sysUserMapper.selectBatchIds(uIds).stream().collect(Collectors.toMap(SysUser::getUid, o -> o));
        }

        List<ShopOrderBatchDeliveryListVo> list = new ArrayList<>(iPage.getRecords().size());
        for (ShopOrderBatchDelivery item : iPage.getRecords()) {
            ShopOrderBatchDeliveryListVo vo = new ShopOrderBatchDeliveryListVo();
            BeanUtils.copyProperties(item, vo);

            vo.setData(JSONUtil.parseObj(item.getData()));
            vo.setTypeName(OrderBatchDeliveryTypeEnum.getNameByValue(item.getType()));
            vo.setStatusName(OrderBatchDeliveryStatusEnum.getNameByValue(item.getStatus()));
            vo.setCreateTime(DateUtils.timestampToString(item.getCreateTime()));

            ShopOrderBatchDeliveryListVo.SysUser user = new ShopOrderBatchDeliveryListVo.SysUser();
            BeanUtils.copyProperties(userMap.getOrDefault(item.getMainId(), new SysUser()), user);
            vo.setUser(user);
            list.add(vo);
        }

        return PageResult.build(iPage, list);
    }

    @Override
    public ShopOrderBatchDeliveryInfoVo info(Integer id) {
        ShopOrderBatchDelivery model = shopOrderBatchDeliveryMapper.selectOne(
                new QueryWrapper<ShopOrderBatchDelivery>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopOrderBatchDeliveryInfoVo vo = new ShopOrderBatchDeliveryInfoVo();
        BeanUtils.copyProperties(model, vo);

        SysUser sysUser = sysUserMapper.selectById(model.getMainId());
        vo.setUser(sysUser);
        vo.setTypeName(OrderBatchDeliveryTypeEnum.getNameByValue(vo.getType()));
        vo.setStatusName(OrderBatchDeliveryStatusEnum.getNameByValue(vo.getStatus()));
        return vo;
    }

    @Override
    public void addBatchOrderDelivery(ShopOrderAddBatchDeliveryParam param) {
        param.setSiteId(RequestUtils.siteId());
        param.setType(OrderBatchDeliveryTypeEnum.DELIVERY);
        param.setMainId(RequestUtils.uid());
        coreOrderBatchDeliveryService.add(param);
    }

}
