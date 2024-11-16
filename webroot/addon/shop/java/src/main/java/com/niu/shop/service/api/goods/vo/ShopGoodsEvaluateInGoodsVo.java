package com.niu.shop.service.api.goods.vo;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.shop.enums.goods.GoodsEvaluateEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ShopGoodsEvaluate列表视图
 */
@Data
public class ShopGoodsEvaluateInGoodsVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long count;

    private List<ShopGoodsEvaluateListVo> list;

}
