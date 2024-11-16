package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品服务参数
 */
@Data
public class ShopGoodsServiceSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceName;

}
