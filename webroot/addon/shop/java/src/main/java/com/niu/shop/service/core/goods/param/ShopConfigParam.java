package com.niu.shop.service.core.goods.param;

import cn.hutool.json.JSON;
import lombok.Data;

import java.io.Serializable;

@Data
public class ShopConfigParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String defaultWord;

    private String searchWords;

    private Integer isEnable;

    private Integer defaultSort;

    private String sortColumn;

    private String sortType;

    private JSON init;
}
