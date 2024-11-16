package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 商品评价参数
*/
@Data
public class ShopGoodsEvaluateParam implements Serializable {

    private static final long serialVersionUID = 1L;


    @NotNull(message = "goodsId参数缺失")
    private Integer goodsId;

    @NotNull(message = "memberHead参数缺失")
    private String memberHead;

    @NotNull(message = "memberName参数缺失")
    private String memberName;

    @NotNull(message = "content参数缺失")
    private String content;

    private String[] images;

    private Integer isAnonymous;

    private Integer scores;

    private String createTime;

}
