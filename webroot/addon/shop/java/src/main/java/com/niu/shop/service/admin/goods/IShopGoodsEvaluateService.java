package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.ShopGoodsEvaluateBatchParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsEvaluateParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsEvaluateInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsEvaluateListVo;
import com.niu.shop.service.admin.goods.param.ShopGoodsEvaluateSearchParam;

/**
 * 商品评价服务接口
 */
public interface IShopGoodsEvaluateService {


    /**
     * 商品评价列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsEvaluateListVo>
     */
    PageResult<ShopGoodsEvaluateListVo> list(PageParam pageParam, ShopGoodsEvaluateSearchParam searchParam);

    /**
     * 商品评价详情
     * @param id 主键ID
     * @return ShopGoodsEvaluateInfoVo
     */
    ShopGoodsEvaluateInfoVo info(Integer id);

    /**
     * 商品评价添加
     * @param addParam 添加参数
     */
    void add(ShopGoodsEvaluateParam addParam);

    /**
     * 商品评价编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopGoodsEvaluateParam editParam);

    /**
     * 商品评价删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 商品评价审核通过
     * @param id
     */
    void auditAdopt(Integer id);

    /**
     * 商品评价审核拒绝
     * @param id
     */
    void auditRefuse(Integer id);

    /**
     * 商品评价申请回复
     * @param explainFirst
     */
    void reply(Integer id, String explainFirst);

    /**
     * 商品评价置顶
     * @param id
     */
    void topping(Integer id);

    /**
     * 商品评价取消置顶
     * @param id
     */
    void cancelTopping(Integer id);

    /**
     * 批量通过
     */
    void batchAdopt(ShopGoodsEvaluateBatchParam shopGoodsEvaluateBatchParam);

    /**
     * 批量拒绝
     */
    void batchRefuse(ShopGoodsEvaluateBatchParam shopGoodsEvaluateBatchParam);

    /**
     * 批量删除
     */
    void batchDel(ShopGoodsEvaluateBatchParam shopGoodsEvaluateBatchParam);
}