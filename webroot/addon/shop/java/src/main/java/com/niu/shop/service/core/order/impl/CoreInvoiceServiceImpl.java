package com.niu.shop.service.core.order.impl;

import com.niu.shop.entity.order.ShopInvoice;
import com.niu.shop.enums.order.InvoiceStatusEnum;
import com.niu.shop.mapper.order.ShopInvoiceMapper;
import com.niu.shop.service.core.order.ICoreInvoiceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
}
