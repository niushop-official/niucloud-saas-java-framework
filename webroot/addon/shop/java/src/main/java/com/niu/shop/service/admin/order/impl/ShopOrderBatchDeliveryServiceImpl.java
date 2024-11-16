package com.niu.shop.service.admin.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.entity.order.ShopOrderBatchDelivery;
import com.niu.shop.mapper.order.ShopOrderBatchDeliveryMapper;
import com.niu.shop.service.admin.order.param.ShopOrderBatchDeliveryParam;
import com.niu.shop.service.admin.order.param.ShopOrderBatchDeliverySearchParam;
import com.niu.shop.service.admin.order.IShopOrderBatchDeliveryService;
import com.niu.shop.service.admin.order.vo.ShopOrderBatchDeliveryInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderBatchDeliveryListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 订单批量发货实现
 */
@Service
public class ShopOrderBatchDeliveryServiceImpl implements IShopOrderBatchDeliveryService {
        
    @Resource
    ShopOrderBatchDeliveryMapper shopOrderBatchDeliveryMapper;

    /**
     * 订单批量发货列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderBatchDeliveryListVo>
     */
    @Override
    public PageResult<ShopOrderBatchDeliveryListVo> list(PageParam pageParam, ShopOrderBatchDeliverySearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopOrderBatchDelivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<ShopOrderBatchDelivery> iPage = shopOrderBatchDeliveryMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopOrderBatchDeliveryListVo> list = new LinkedList<>();
        for (ShopOrderBatchDelivery item : iPage.getRecords()) {
            ShopOrderBatchDeliveryListVo vo = new ShopOrderBatchDeliveryListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 订单批量发货详情
     * @param id 主键
     * @return ShopOrderBatchDeliveryInfoVo
     */
    @Override
    public ShopOrderBatchDeliveryInfoVo info(Integer id) {
        ShopOrderBatchDelivery model = shopOrderBatchDeliveryMapper.selectOne(
                new QueryWrapper<ShopOrderBatchDelivery>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopOrderBatchDeliveryInfoVo vo = new ShopOrderBatchDeliveryInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 订单批量发货添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopOrderBatchDeliveryParam addParam) {
        ShopOrderBatchDelivery model = new ShopOrderBatchDelivery();
        model.setSiteId(addParam.getSiteId());
        model.setMainId(addParam.getMainId());
        model.setStatus(addParam.getStatus());
        model.setType(addParam.getType());
        model.setTotalNum(addParam.getTotalNum());
        model.setSuccessNum(addParam.getSuccessNum());
        model.setFailNum(addParam.getFailNum());
        model.setData(addParam.getData());
        model.setOutput(addParam.getOutput());
        model.setFailOutput(addParam.getFailOutput());
        model.setFailRemark(addParam.getFailRemark());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        shopOrderBatchDeliveryMapper.insert(model);
    }

    /**
     * 订单批量发货编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopOrderBatchDeliveryParam editParam) {
        ShopOrderBatchDelivery model = shopOrderBatchDeliveryMapper.selectOne(
                new QueryWrapper<ShopOrderBatchDelivery>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        
        model.setSiteId(editParam.getSiteId());
        model.setMainId(editParam.getMainId());
        model.setStatus(editParam.getStatus());
        model.setType(editParam.getType());
        model.setTotalNum(editParam.getTotalNum());
        model.setSuccessNum(editParam.getSuccessNum());
        model.setFailNum(editParam.getFailNum());
        model.setData(editParam.getData());
        model.setOutput(editParam.getOutput());
        model.setFailOutput(editParam.getFailOutput());
        model.setFailRemark(editParam.getFailRemark());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        shopOrderBatchDeliveryMapper.updateById(model);
    }

    /**
     * 订单批量发货删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        ShopOrderBatchDelivery model = shopOrderBatchDeliveryMapper.selectOne(
                new QueryWrapper<ShopOrderBatchDelivery>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        shopOrderBatchDeliveryMapper.delete(new QueryWrapper<ShopOrderBatchDelivery>().eq("id", id));
    }

}
