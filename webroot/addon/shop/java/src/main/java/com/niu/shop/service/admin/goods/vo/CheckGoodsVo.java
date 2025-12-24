package com.niu.shop.service.admin.goods.vo;

import lombok.Data;

import java.io.Serializable;

/**
* 检测商品
*/
@Data
public class CheckGoodsVo implements Serializable {

private static final long serialVersionUID = 1L;

        private Integer code;

        private String message;

}
