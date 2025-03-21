package com.niu.shop.service.admin.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.entity.order.ShopOrderDiscount;
import com.niu.shop.mapper.order.ShopOrderDiscountMapper;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountParam;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountSearchParam;
import com.niu.shop.service.admin.order.IShopOrderDiscountService;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 订单优惠实现
 */
@Service
public class ShopOrderDiscountServiceImpl implements IShopOrderDiscountService {
        
    @Resource
    ShopOrderDiscountMapper shopOrderDiscountMapper;

    /**
     * 订单优惠列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderDiscountListVo>
     */
    @Override
    public PageResult<ShopOrderDiscountListVo> list(PageParam pageParam, ShopOrderDiscountSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopOrderDiscount> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<ShopOrderDiscount> iPage = shopOrderDiscountMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopOrderDiscountListVo> list = new LinkedList<>();
        for (ShopOrderDiscount item : iPage.getRecords()) {
            ShopOrderDiscountListVo vo = new ShopOrderDiscountListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 订单优惠详情
     * @param id 主键
     * @return ShopOrderDiscountInfoVo
     */
    @Override
    public ShopOrderDiscountInfoVo info(Integer id) {
        ShopOrderDiscount model = shopOrderDiscountMapper.selectOne(
                new QueryWrapper<ShopOrderDiscount>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopOrderDiscountInfoVo vo = new ShopOrderDiscountInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 订单优惠添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopOrderDiscountParam addParam) {
        ShopOrderDiscount model = new ShopOrderDiscount();
        model.setSiteId(addParam.getSiteId());
        model.setOrderId(addParam.getOrderId());
        model.setOrderGoodsIds(addParam.getOrderGoodsIds());
        model.setType(addParam.getType());
        model.setNum(addParam.getNum());
        model.setMoney(addParam.getMoney());
        model.setDiscountType(addParam.getDiscountType());
        model.setDiscountTypeId(addParam.getDiscountTypeId());
        model.setContent(addParam.getContent());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setStatus(addParam.getStatus());
        shopOrderDiscountMapper.insert(model);
    }

    /**
     * 订单优惠编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopOrderDiscountParam editParam) {
        ShopOrderDiscount model = shopOrderDiscountMapper.selectOne(
                new QueryWrapper<ShopOrderDiscount>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        
        model.setSiteId(editParam.getSiteId());
        model.setOrderId(editParam.getOrderId());
        model.setOrderGoodsIds(editParam.getOrderGoodsIds());
        model.setType(editParam.getType());
        model.setNum(editParam.getNum());
        model.setMoney(editParam.getMoney());
        model.setDiscountType(editParam.getDiscountType());
        model.setDiscountTypeId(editParam.getDiscountTypeId());
        model.setContent(editParam.getContent());
        model.setStatus(editParam.getStatus());
        shopOrderDiscountMapper.updateById(model);
    }

    /**
     * 订单优惠删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        ShopOrderDiscount model = shopOrderDiscountMapper.selectOne(
                new QueryWrapper<ShopOrderDiscount>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        shopOrderDiscountMapper.delete(new QueryWrapper<ShopOrderDiscount>().eq("id", id));
    }

}
