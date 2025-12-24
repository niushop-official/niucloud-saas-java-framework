package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 商品参数参数
 */
@Data
public class ShopGoodsAttrSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String attrName;

    private List<Integer> attrIdArr;

    private String order;

    private String sort;

}
