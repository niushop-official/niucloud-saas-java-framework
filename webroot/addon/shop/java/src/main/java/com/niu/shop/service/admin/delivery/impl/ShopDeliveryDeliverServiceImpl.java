package com.niu.shop.service.admin.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.delivery.ShopDeliveryDeliver;
import com.niu.shop.mapper.delivery.ShopDeliveryDeliverMapper;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryDeliverParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryDeliverSearchParam;
import com.niu.shop.service.admin.delivery.IShopDeliveryDeliverService;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryDeliverInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryDeliverListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 配送员实现
 */
@Service
public class ShopDeliveryDeliverServiceImpl implements IShopDeliveryDeliverService {
        
    @Resource
    ShopDeliveryDeliverMapper shopDeliveryDeliverMapper;

    /**
     * 配送员列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryDeliverListVo>
     */
    @Override
    public PageResult<ShopDeliveryDeliverListVo> list(PageParam pageParam, ShopDeliveryDeliverSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopDeliveryDeliver> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getDeliverMobile())) queryWrapper.like("deliver_mobile", searchParam.getDeliverMobile());
        if (ObjectUtil.isNotEmpty(searchParam.getDeliverName())) queryWrapper.like("deliver_name", searchParam.getDeliverName());
        queryWrapper.orderByDesc("deliver_id");

        IPage<ShopDeliveryDeliver> iPage = shopDeliveryDeliverMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopDeliveryDeliverListVo> list = new LinkedList<>();
        for (ShopDeliveryDeliver item : iPage.getRecords()) {
            ShopDeliveryDeliverListVo vo = new ShopDeliveryDeliverListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 配送员详情
     * @param id 主键
     * @return ShopDeliveryDeliverInfoVo
     */
    @Override
    public ShopDeliveryDeliverInfoVo info(Integer id) {
        ShopDeliveryDeliver model = shopDeliveryDeliverMapper.selectOne(
                new QueryWrapper<ShopDeliveryDeliver>()
                    .eq("deliver_id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "配送员不存在");

        ShopDeliveryDeliverInfoVo vo = new ShopDeliveryDeliverInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 配送员添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopDeliveryDeliverParam addParam) {
        ShopDeliveryDeliver model = new ShopDeliveryDeliver();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        shopDeliveryDeliverMapper.insert(model);
    }

    /**
     * 配送员编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopDeliveryDeliverParam editParam) {
        ShopDeliveryDeliver model = shopDeliveryDeliverMapper.selectOne(
                new QueryWrapper<ShopDeliveryDeliver>()
                        .eq("deliver_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "配送员不存在");
        BeanUtils.copyProperties(editParam, model);
        model.setModifyTime(System.currentTimeMillis() / 1000);
        shopDeliveryDeliverMapper.updateById(model);
    }

    /**
     * 配送员删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        shopDeliveryDeliverMapper.delete(new QueryWrapper<ShopDeliveryDeliver>().eq("deliver_id", id).eq("site_id", RequestUtils.siteId()));
    }

}
