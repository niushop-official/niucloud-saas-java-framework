package com.niu.shop.service.core.order.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.shop.entity.order.ShopInvoice;
import com.niu.shop.enums.order.InvoiceStatusEnum;
import com.niu.shop.enums.order.InvoicedEnum;
import com.niu.shop.mapper.order.ShopInvoiceMapper;
import com.niu.shop.service.core.order.ICoreInvoiceService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class CoreInvoiceServiceImpl implements ICoreInvoiceService {

    @Resource
    ShopInvoiceMapper shopInvoiceMapper;

    /**
     * 发票生效
     *
     * @param id
     */
    @Override
    public void open(Integer id) {
        ShopInvoice model = new ShopInvoice();
        model.setId(id);
        model.setStatus(InvoiceStatusEnum.OPEN.getStatus());
        shopInvoiceMapper.updateById(model);
    }

    /**
     * 发票失效
     *
     * @param id
     */
    @Override
    public void close(Integer id) {
        ShopInvoice model = new ShopInvoice();
        model.setId(id);
        model.setStatus(InvoiceStatusEnum.WAIT_OPEN.getStatus());
        shopInvoiceMapper.updateById(model);
    }

    @Override
    public void audit(List<Integer> ids) {
        UpdateWrapper<ShopInvoice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        updateWrapper.eq("is_invoice", InvoicedEnum.WAIT_AUDIT.getStatus());
        updateWrapper.set("is_invoice", InvoicedEnum.WAIT_INVOICE.getStatus());
        shopInvoiceMapper.update(null, updateWrapper);
    }
}
