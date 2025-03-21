package com.niu.shop.service.admin.shop_address.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.shop_address.ShopAddress;
import com.niu.shop.mapper.shop_address.ShopAddressMapper;
import com.niu.shop.service.admin.shop_address.param.ShopAddressParam;
import com.niu.shop.service.admin.shop_address.param.ShopAddressSearchParam;
import com.niu.shop.service.admin.shop_address.IShopAddressService;
import com.niu.shop.service.admin.shop_address.vo.ShopAddressInfoVo;
import com.niu.shop.service.admin.shop_address.vo.ShopAddressListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * 商家地址库实现
 */
@Service
public class ShopAddressServiceImpl implements IShopAddressService {
        
    @Resource
    ShopAddressMapper shopAddressMapper;

    /**
     * 商家地址库列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopAddressListVo>
     */
    @Override
    public PageResult<ShopAddressListVo> page(PageParam pageParam, ShopAddressSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getMobile())) queryWrapper.eq("mobile", searchParam.getMobile());
        if (ObjectUtil.isNotEmpty(searchParam.getFullAddress())) queryWrapper.like("full_address", searchParam.getFullAddress());
        queryWrapper.orderByDesc("id");

        IPage<ShopAddress> iPage = shopAddressMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopAddressListVo> list = new LinkedList<>();
        for (ShopAddress item : iPage.getRecords()) {
            ShopAddressListVo vo = new ShopAddressListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商家地址库详情
     *
     * @param id 主键
     * @return ShopAddressInfoVo
     */
    @Override
    public ShopAddressInfoVo info(Integer id) {
        ShopAddress model = shopAddressMapper.selectOne(
                new QueryWrapper<ShopAddress>()
                    .eq("id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "地址不存在");

        ShopAddressInfoVo vo = new ShopAddressInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 获取默认发货地址
     *
     * @return ShopAddressInfoVo
     */
    @Override
    public ShopAddressInfoVo defaultDelivery() {
        ShopAddress model = shopAddressMapper.selectOne(
                new QueryWrapper<ShopAddress>()
                        .eq("is_delivery_address", 1)
                        .eq("is_default_delivery", 1)
                        .eq("site_id", RequestUtils.siteId()));

        if (model == null) return null;

        ShopAddressInfoVo vo = new ShopAddressInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 商家地址库添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(ShopAddressParam addParam) {
        ShopAddress model = new ShopAddress();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());

        if (addParam.getIsDefaultDelivery().equals(1)) {
            ShopAddress updateModel = new ShopAddress();
            updateModel.setIsDefaultDelivery(0);
            shopAddressMapper.update(updateModel, new QueryWrapper<ShopAddress>().eq("site_id", RequestUtils.siteId()).ne("is_default_delivery", 0));
        }
        if (addParam.getIsDefaultRefund().equals(1)) {
            ShopAddress updateModel = new ShopAddress();
            updateModel.setIsDefaultRefund(0);
            shopAddressMapper.update(updateModel, new QueryWrapper<ShopAddress>().eq("site_id", RequestUtils.siteId()).ne("is_default_refund", 0));
        }

        shopAddressMapper.insert(model);
    }

    /**
     * 商家地址库编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopAddressParam editParam) {
        ShopAddress model = shopAddressMapper.selectOne(
                new QueryWrapper<ShopAddress>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "地址不存在");
        BeanUtils.copyProperties(editParam, model);

        if (editParam.getIsDefaultDelivery().equals(1)) {
            ShopAddress updateModel = new ShopAddress();
            updateModel.setIsDefaultDelivery(0);
            shopAddressMapper.update(updateModel, new QueryWrapper<ShopAddress>().eq("site_id", RequestUtils.siteId()).ne("is_default_delivery", 0));
        }
        if (editParam.getIsDefaultRefund().equals(1)) {
            ShopAddress updateModel = new ShopAddress();
            updateModel.setIsDefaultRefund(0);
            shopAddressMapper.update(updateModel, new QueryWrapper<ShopAddress>().eq("site_id", RequestUtils.siteId()).ne("is_default_refund", 0));
        }
        shopAddressMapper.updateById(model);
    }

    /**
     * 商家地址库删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        shopAddressMapper.delete(new QueryWrapper<ShopAddress>().eq("id", id).eq("site_id", RequestUtils.siteId()));
    }

    /**
     * 退款地址列表
     *
     * @return
     */
    @Override
    public List<ShopAddressListVo> refundAddress() {
        return CollectUtils.convert(shopAddressMapper.selectList(new QueryWrapper<ShopAddress>().eq("is_refund_address", 1).eq("site_id", RequestUtils.siteId())), ShopAddressListVo.class);
    }

}
