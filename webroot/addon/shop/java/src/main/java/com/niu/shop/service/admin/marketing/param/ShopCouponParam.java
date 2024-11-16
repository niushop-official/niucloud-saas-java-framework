package com.niu.shop.service.admin.marketing.param;
import cn.hutool.json.JSONArray;
import lombok.Data;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 优惠券参数
*/
@Data
public class ShopCouponParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    @NotNull(message = "title参数缺失")
    private String title;


    @NotNull(message = "remainCount参数缺失")
    private Integer remainCount;

    @NotNull(message = "limitCount参数缺失")
    private Integer limitCount;

    @NotNull(message = "price参数缺失")
    private BigDecimal price;

    @NotNull(message = "minConditionMoney参数缺失")
    private BigDecimal minConditionMoney;

    @NotNull(message = "type参数缺失")
    private Integer type;

    @NotNull(message = "receiveType参数缺失")
    private Integer receiveType;

    private String[] receiveTime;

    private Integer receiveTypeTime;

    @NotNull(message = "validType参数缺失")
    private Integer validType;

    private String validTime;

    @NotNull(message = "length参数缺失")
    private Integer length;

    private Integer threshold;

    private Integer limit;

    private Integer[] goodsIds;

    private JSONArray goodsCategoryIds;

}
