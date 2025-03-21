package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 商品分类参数
*/
@Data
public class EditGoodsCategorySortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Item> categorySortArray;

    @Data
    public static class Item {
        private Integer categoryId;
        private Integer sort;
    }
}
