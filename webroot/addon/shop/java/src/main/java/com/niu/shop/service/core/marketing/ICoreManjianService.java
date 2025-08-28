package com.niu.shop.service.core.marketing;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.shop.entity.manjian.ShopManjian;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.service.admin.goods.vo.ShopManjianRuleContentVo;
import com.niu.shop.service.admin.order.vo.ShopOrderInfoVo;
import com.niu.shop.service.api.cart.vo.ShopCartCalculateVo;
import com.niu.shop.service.core.marketing.param.GetManjianInfoParam;
import com.niu.shop.service.core.marketing.vo.ShopManjianInfoVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * core满减服务接口
 */
public interface ICoreManjianService extends IService<ShopManjian> {

    /**
     * 满减活动优惠计算
     *
     * @param order 订单相关信息
     */
    void calculate(OrderCreateDataVo order);

    /**
     * 满减活动商品计算
     *
     * @param order                   订单相关信息
     * @param manjianInfo             满减活动相关信息（
     * @param isFreeShipping          是否免邮
     * @param level                   优惠层级
     * @param discountMoney           优惠金额
     * @param matchSkuIds             匹配的商品SKU ID列表
     * @param matchOrderGoodsMoney    匹配的订单商品总金额
     * @param manjianOneDiscountMoney 单个满减活动的优惠金额
     */
    BigDecimal manjianGoodsCalculate(final OrderCreateDataVo order, ShopManjianInfoVo manjianInfo, boolean isFreeShipping, int level, BigDecimal discountMoney, List<Integer> matchSkuIds, BigDecimal matchOrderGoodsMoney, final BigDecimal manjianOneDiscountMoney);

    /**
     * 满减活动商品赠品内容处理
     *
     * @param order     订单相关信息
     * @param gift      赠品相关信息（包含各种赠品类型的数据，如积分、余额、优惠券、商品等）
     * @param manjianId 满减活动ID
     * @param level     优惠层级
     * @param skuIds    商品SKU ID列表
     */
    void getGiftContent(OrderCreateDataVo order, Map<String, Object> gift, Integer manjianId, int level, List<Integer> skuIds);

    /**
     * 添加满减活动赠送记录
     *
     * @param order 包含相关数据的对象（具体结构需根据实际业务定义，比如包含订单数据、折扣数据等）
     * @return true表示操作成功
     */
    boolean addGiveRecords(OrderCreateDataVo order);

    /**
     * 满减活动赠品发放
     *
     * @param order 包含相关数据的对象（如站点ID、订单ID、会员ID等必要信息）
     * @return true表示操作成功
     */
    boolean giftGrant(ShopOrder order);

    /**
     * 满减活动退款校验
     *
     * @param order
     * @return 退款赠品相关信息列表
     */
    Map<Integer, JSONObject> refundCheck(ShopOrderRefund order);

    /**
     * 订单退款后退还赠品
     *
     * @param order
     * @return true表示操作成功
     */
    boolean refundGift(ShopOrderRefund order);

    /**
     * 获取满减送订单项赠送信息
     *
     * @param orderGoodsInfo 订单项商品相关信息
     * @param orderGoodsList 订单商品列表信息
     * @param siteId         站点ID
     * @param memberId       会员ID
     */
    void getOrderGoodsGiveInfo(ShopOrderInfoVo.OrderGoods orderGoodsInfo, List<ShopOrderInfoVo.OrderGoods> orderGoodsList, Integer siteId, Integer memberId);

    /**
     * 满减优惠处理
     *
     * @param data     包含相关数据的对象
     * @param siteId   站点ID
     * @param memberId 会员ID
     * @return 处理后的包含商品列表和促销金额等更新后的数据对象（具体类型需根据实际业务定义）
     */
    ShopCartCalculateVo manjianPromotion(ShopCartCalculateVo data, Integer siteId, Integer memberId);


    Map<String, String> getManjianDiscountMoney(ShopManjian manjianInfo, ShopCartCalculateVo data);

    /**
     * 判断是否可参加活动
     *
     * @param manjianInfo 满减活动相关信息
     * @param siteId      站点ID
     * @param memberId    会员ID
     * @return true表示可参加活动，false表示不可参加
     */
    boolean canJoinManjian(ShopManjian manjianInfo, Integer siteId, Integer memberId);

    /**
     * 金额格式化
     *
     * @param money 要格式化的金额
     * @return 格式化后的金额
     */
    BigDecimal moneyFormat(BigDecimal money);

    ShopManjianRuleContentVo getManjianInfo(GetManjianInfoParam param);
}