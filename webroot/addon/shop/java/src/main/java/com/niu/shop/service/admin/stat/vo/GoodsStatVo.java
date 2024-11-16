package com.niu.shop.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsStatVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long saleGoodsNum = 0L;

    private Long warehouseGoodsNum = 0L;

}
