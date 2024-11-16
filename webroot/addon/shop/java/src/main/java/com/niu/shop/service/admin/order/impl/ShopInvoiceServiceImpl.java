package com.niu.shop.service.admin.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.shop.entity.order.ShopInvoice;
import com.niu.shop.enums.order.InvoiceStatusEnum;
import com.niu.shop.mapper.order.ShopInvoiceMapper;
import com.niu.shop.service.admin.order.IShopInvoiceService;
import com.niu.shop.service.admin.order.param.ShopInvoiceParam;
import com.niu.shop.service.admin.order.param.ShopInvoiceSearchParam;
import com.niu.shop.service.admin.order.vo.ShopInvoiceInfoVo;
import com.niu.shop.service.admin.order.vo.ShopInvoiceListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
public class ShopInvoiceServiceImpl implements IShopInvoiceService {


    @Resource
    ShopInvoiceMapper shopInvoiceMapper;

    @Override
    public PageResult<ShopInvoiceListVo> getPage(PageParam pageParam, ShopInvoiceSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopInvoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("status", InvoiceStatusEnum.OPEN.getStatus());

        if (ObjectUtil.isNotEmpty(searchParam.getHeaderType())) queryWrapper.eq("header_type", searchParam.getHeaderType());
        if (ObjectUtil.isNotEmpty(searchParam.getHeaderName())) queryWrapper.like("header_name", searchParam.getHeaderName());
        if (ObjectUtil.isNotEmpty(searchParam.getIsInvoice()) && !searchParam.getIsInvoice().equals("all")) queryWrapper.eq("is_invoice", searchParam.getIsInvoice());
        if (ObjectUtil.isNotEmpty(searchParam.getInvoiceTime())) QueryMapperUtils.buildByTime(queryWrapper, "invoice_time", searchParam.getInvoiceTime());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getInvoiceTime());

        queryWrapper.orderByDesc("id");
        IPage<ShopInvoice> iPage = shopInvoiceMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopInvoiceListVo> list = new LinkedList<>();
        for (ShopInvoice item : iPage.getRecords()) {
            ShopInvoiceListVo vo = new ShopInvoiceListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public ShopInvoiceInfoVo getInfo(Integer id) {
        ShopInvoice model = shopInvoiceMapper.selectOne(
                new QueryWrapper<ShopInvoice>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopInvoiceInfoVo vo = new ShopInvoiceInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public void invoicing(Integer id, ShopInvoiceParam shopInvoiceParam) {
        ShopInvoice model = shopInvoiceMapper.selectOne(
                new QueryWrapper<ShopInvoice>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("status", 2)
                        .last("limit 1"));
        Assert.notNull(model, "数据不存在");
        if(model.getIsInvoice() == 1) throw new AdminException("订单已开票");
        model.setIsInvoice(1);
        model.setInvoiceNumber(shopInvoiceParam.getInvoiceNumber());
        model.setInvoiceVoucher(shopInvoiceParam.getInvoiceVoucher());
        model.setRemark(shopInvoiceParam.getRemark());
        model.setInvoiceTime(System.currentTimeMillis() / 1000);
        shopInvoiceMapper.updateById(model);

    }
}
