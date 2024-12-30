package com.niu.shop.service.admin.marketing.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 优惠券参数
 */
@Data
public class ShopCouponSelectParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private List<Integer> verifyCouponIds;


}
