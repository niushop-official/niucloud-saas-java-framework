package com.niu.shop.service.api.goods;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.api.goods.param.ShopGoodsCategorySearchParam;
import com.niu.shop.service.api.goods.param.ShopGoodsEvaluateParam;
import com.niu.shop.service.api.goods.param.ShopGoodsEvaluateSearchParam;
import com.niu.shop.service.api.goods.vo.*;
import com.niu.shop.service.core.goods.vo.GoodsCategoryConfigVo;

import java.util.List;
import java.util.Map;

/**
 * 商品评价接口
 */
public interface IGoodsEvaluateService {

    /**
     * 商品评价列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<ShopGoodsEvaluateListVo> getPages(PageParam pageParam, ShopGoodsEvaluateSearchParam searchParam);

    /**
     * 商品评价信息
     * @param id
     * @return
     */
    ShopGoodsEvaluateInfoVo getInfo(Integer id);

    /**
     * 添加商品评价
     * @param shopGoodsEvaluateParam
     */
    void add(ShopGoodsEvaluateParam shopGoodsEvaluateParam);

    /**
     * 获取商品评价数量
     * @param goodsId
     * @return
     */
    Map<String, Long> getCount(Integer goodsId);

    /**
     * 商品显示评价
     * @param goodsId
     * @return
     */
    ShopGoodsEvaluateInGoodsVo getList(Integer goodsId);

    /**
     * 订单展示评价信息
     * @param orderId
     * @return
     */
    List<ShopGoodsEvaluateDetailVo> getDetail(Integer orderId);

}
