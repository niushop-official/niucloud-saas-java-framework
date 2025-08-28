package com.niu.shop.service.core.goods.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShopConfigVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String default_word;

    private String[] search_words;
}