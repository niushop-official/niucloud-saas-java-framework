package com.niu.shop.service.admin.marketing.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.entity.exchange.ShopPointExchangeOrder;
import com.niu.shop.mapper.exchange.ShopPointExchangeOrderMapper;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeOrderParam;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeOrderSearchParam;
import com.niu.shop.service.admin.marketing.IShopPointExchangeOrderService;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeOrderInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeOrderListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 积分兑换订单实现
 */
@Service
public class ShopPointExchangeOrderServiceImpl implements IShopPointExchangeOrderService {
        
    @Resource
    ShopPointExchangeOrderMapper shopPointExchangeOrderMapper;

    /**
     * 积分兑换订单列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopPointExchangeOrderListVo>
     */
    @Override
    public PageResult<ShopPointExchangeOrderListVo> list(PageParam pageParam, ShopPointExchangeOrderSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopPointExchangeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("order_id");

        IPage<ShopPointExchangeOrder> iPage = shopPointExchangeOrderMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopPointExchangeOrderListVo> list = new LinkedList<>();
        for (ShopPointExchangeOrder item : iPage.getRecords()) {
            ShopPointExchangeOrderListVo vo = new ShopPointExchangeOrderListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 积分兑换订单详情
     * @param id 主键
     * @return ShopPointExchangeOrderInfoVo
     */
    @Override
    public ShopPointExchangeOrderInfoVo info(Integer id) {
        ShopPointExchangeOrder model = shopPointExchangeOrderMapper.selectOne(
                new QueryWrapper<ShopPointExchangeOrder>()
                    .eq("order_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopPointExchangeOrderInfoVo vo = new ShopPointExchangeOrderInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 积分兑换订单添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopPointExchangeOrderParam addParam) {
        ShopPointExchangeOrder model = new ShopPointExchangeOrder();
        model.setOrderNo(addParam.getOrderNo());
        model.setOutTradeNo(addParam.getOutTradeNo());
        model.setSiteId(addParam.getSiteId());
        model.setExchangeId(addParam.getExchangeId());
        model.setExchangeName(addParam.getExchangeName());
        model.setExchangeImage(addParam.getExchangeImage());
        model.setType(addParam.getType());
        model.setMemberId(addParam.getMemberId());
        model.setMemberAddressId(addParam.getMemberAddressId());
        model.setRelateId(addParam.getRelateId());
        model.setRelateOrderId(addParam.getRelateOrderId());
        model.setPoint(addParam.getPoint());
        model.setPrice(addParam.getPrice());
        model.setBalance(addParam.getBalance());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setPayTime(addParam.getPayTime());
        model.setCloseTime(addParam.getCloseTime());
        model.setNum(addParam.getNum());
        model.setStatus(addParam.getStatus());
        model.setOrderMoney(addParam.getOrderMoney());
        shopPointExchangeOrderMapper.insert(model);
    }

    /**
     * 积分兑换订单编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopPointExchangeOrderParam editParam) {
        ShopPointExchangeOrder model = shopPointExchangeOrderMapper.selectOne(
                new QueryWrapper<ShopPointExchangeOrder>()
                    .eq("order_id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setOrderId(id);
        model.setOrderId(editParam.getOrderId());
        model.setOrderNo(editParam.getOrderNo());
        model.setOutTradeNo(editParam.getOutTradeNo());
        model.setSiteId(editParam.getSiteId());
        model.setExchangeId(editParam.getExchangeId());
        model.setExchangeName(editParam.getExchangeName());
        model.setExchangeImage(editParam.getExchangeImage());
        model.setType(editParam.getType());
        model.setMemberId(editParam.getMemberId());
        model.setMemberAddressId(editParam.getMemberAddressId());
        model.setRelateId(editParam.getRelateId());
        model.setRelateOrderId(editParam.getRelateOrderId());
        model.setPoint(editParam.getPoint());
        model.setPrice(editParam.getPrice());
        model.setBalance(editParam.getBalance());
        model.setPayTime(editParam.getPayTime());
        model.setCloseTime(editParam.getCloseTime());
        model.setNum(editParam.getNum());
        model.setStatus(editParam.getStatus());
        model.setOrderMoney(editParam.getOrderMoney());
        shopPointExchangeOrderMapper.updateById(model);
    }

    /**
     * 积分兑换订单删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        ShopPointExchangeOrder model = shopPointExchangeOrderMapper.selectOne(
                new QueryWrapper<ShopPointExchangeOrder>()
                    .eq("order_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        shopPointExchangeOrderMapper.delete(new QueryWrapper<ShopPointExchangeOrder>().eq("order_id", id));
    }

}
