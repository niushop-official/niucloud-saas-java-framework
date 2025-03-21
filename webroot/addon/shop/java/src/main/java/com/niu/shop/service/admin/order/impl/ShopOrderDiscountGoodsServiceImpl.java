package com.niu.shop.service.admin.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.entity.order.ShopOrderDiscountGoods;
import com.niu.shop.mapper.order.ShopOrderDiscountGoodsMapper;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountGoodsParam;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountGoodsSearchParam;
import com.niu.shop.service.admin.order.IShopOrderDiscountGoodsService;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountGoodsInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountGoodsListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 订单项优惠实现
 */
@Service
public class ShopOrderDiscountGoodsServiceImpl implements IShopOrderDiscountGoodsService {
        
    @Resource
    ShopOrderDiscountGoodsMapper shopOrderDiscountGoodsMapper;

    /**
     * 订单项优惠列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderDiscountGoodsListVo>
     */
    @Override
    public PageResult<ShopOrderDiscountGoodsListVo> list(PageParam pageParam, ShopOrderDiscountGoodsSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopOrderDiscountGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<ShopOrderDiscountGoods> iPage = shopOrderDiscountGoodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopOrderDiscountGoodsListVo> list = new LinkedList<>();
        for (ShopOrderDiscountGoods item : iPage.getRecords()) {
            ShopOrderDiscountGoodsListVo vo = new ShopOrderDiscountGoodsListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 订单项优惠详情
     * @param id 主键
     * @return ShopOrderDiscountGoodsInfoVo
     */
    @Override
    public ShopOrderDiscountGoodsInfoVo info(Integer id) {
        ShopOrderDiscountGoods model = shopOrderDiscountGoodsMapper.selectOne(
                new QueryWrapper<ShopOrderDiscountGoods>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopOrderDiscountGoodsInfoVo vo = new ShopOrderDiscountGoodsInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 订单项优惠添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopOrderDiscountGoodsParam addParam) {
        ShopOrderDiscountGoods model = new ShopOrderDiscountGoods();
        model.setSiteId(addParam.getSiteId());
        model.setOrderDiscountId(addParam.getOrderDiscountId());
        model.setOrderId(addParam.getOrderId());
        model.setOrderGoodsId(addParam.getOrderGoodsId());
        model.setType(addParam.getType());
        model.setNum(addParam.getNum());
        model.setMoney(addParam.getMoney());
        shopOrderDiscountGoodsMapper.insert(model);
    }

    /**
     * 订单项优惠编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopOrderDiscountGoodsParam editParam) {
        ShopOrderDiscountGoods model = shopOrderDiscountGoodsMapper.selectOne(
                new QueryWrapper<ShopOrderDiscountGoods>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        
        model.setSiteId(editParam.getSiteId());
        model.setOrderDiscountId(editParam.getOrderDiscountId());
        model.setOrderId(editParam.getOrderId());
        model.setOrderGoodsId(editParam.getOrderGoodsId());
        model.setType(editParam.getType());
        model.setNum(editParam.getNum());
        model.setMoney(editParam.getMoney());
        shopOrderDiscountGoodsMapper.updateById(model);
    }

    /**
     * 订单项优惠删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        ShopOrderDiscountGoods model = shopOrderDiscountGoodsMapper.selectOne(
                new QueryWrapper<ShopOrderDiscountGoods>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        shopOrderDiscountGoodsMapper.delete(new QueryWrapper<ShopOrderDiscountGoods>().eq("id", id));
    }

}
