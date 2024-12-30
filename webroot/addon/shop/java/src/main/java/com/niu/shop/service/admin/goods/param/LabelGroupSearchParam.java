package com.niu.shop.service.admin.goods.param;

import lombok.Data;
import java.io.Serializable;

/**
 * 商品标签分组表参数
 */
@Data
public class LabelGroupSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String groupName;

    private String sort;

    private String order;


}
