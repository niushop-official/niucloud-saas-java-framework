package com.niu.shop.service.core.order;

public interface ICoreInvoiceService {

    /**
     * 发票生效
     *
     * @param id
     */
    void open(Integer id);

    /**
     * 发票失效
     *
     * @param id
     */
    void close(Integer id);
}
