<?php
// +----------------------------------------------------------------------
// | Niucloud-admin 企业快速开发的多应用管理平台
// +----------------------------------------------------------------------
// | 官方网址：https://www.niucloud.com
// +----------------------------------------------------------------------
// | niucloud团队 版权所有 开源版本可自由商用
// +----------------------------------------------------------------------
// | Author: Niucloud Team
// +----------------------------------------------------------------------

use app\api\middleware\ApiChannel;
use app\api\middleware\ApiCheckToken;
use app\api\middleware\ApiLog;
use think\facade\Route;


/**
 * 商城系统
 */
Route::group('shop', function() {

    /***************************************************** 商品 ****************************************************/

    // 获取商品列表
    Route::get('goods/pages', 'addon\shop\app\api\controller\goods\Goods@pages');

    // 获取商品详情
    Route::get('goods/detail', 'addon\shop\app\api\controller\goods\Goods@detail');

    // 获取商品详情
    Route::get('goods/sku/:sku_id', 'addon\shop\app\api\controller\goods\Goods@sku');

    // 获取商品分类配置
    Route::get('goods/category/config', 'addon\shop\app\api\controller\goods\GoodsCategory@getGoodsCategoryConfig');

    // 获取商品分类树结构
    Route::get('goods/category/tree', 'addon\shop\app\api\controller\goods\GoodsCategory@tree');

    // 获取商品列表供组件调用
    Route::get('goods/components', 'addon\shop\app\api\controller\goods\Goods@components');

    /***************************************************** 评价 ****************************************************/
    // 获取 评价设置
    Route::get('goods/evaluate/config', 'addon\shop\app\api\controller\Config@evaluate');
    // 获取 评价列表
    Route::get('goods/evaluate', 'addon\shop\app\api\controller\goods\Evaluate@pages');
    // 获取 评价列表
    Route::get('goods/evaluate/list', 'addon\shop\app\api\controller\goods\Evaluate@lists');
    // 获取 评价数量
    Route::get('goods/evaluate/count', 'addon\shop\app\api\controller\goods\Evaluate@count');
    // 获取 评价详情（评价）
    Route::get('goods/evaluate/:id', 'addon\shop\app\api\controller\goods\Evaluate@info');
    // 添加 商品评价
    Route::post('goods/evaluate', 'addon\shop\app\api\controller\goods\Evaluate@add');
    // 评价 （订单页）
    Route::get('order/evaluate/:id', 'addon\shop\app\api\controller\goods\Evaluate@getEvaluate');

    /***************************************************** 优惠券 ****************************************************/
    // 获取优惠券列表
    Route::get('coupon', 'addon\shop\app\api\controller\marketing\Coupon@lists');
    //详情
    Route::get('coupon/:id', 'addon\shop\app\api\controller\marketing\Coupon@detail');

    Route::get('config/invoice', 'addon\shop\app\api\controller\Config@invoice');

})->middleware(ApiChannel::class)
    ->middleware(ApiCheckToken::class)//false表示不验证登录
    ->middleware(ApiLog::class);


Route::group('shop', function() {

    /***************************************************** 购物车 ****************************************************/

    // 添加购物车
    Route::post('cart', 'addon\shop\app\api\controller\cart\Cart@add');

    // 编辑购物车数量
    Route::put('cart', 'addon\shop\app\api\controller\cart\Cart@edit');

    // 购物车删除
    Route::put('cart/delete', 'addon\shop\app\api\controller\cart\Cart@del');

    // 清空购物车
    Route::delete('cart/clear', 'addon\shop\app\api\controller\cart\Cart@clear');

    // 购物车列表
    Route::get('cart', 'addon\shop\app\api\controller\cart\Cart@lists');

    // 购物车商品列表
    Route::get('cart/goods', 'addon\shop\app\api\controller\cart\Cart@goodsLists');

    // 购物车数量
    Route::get('cart/sum', 'addon\shop\app\api\controller\cart\Cart@sum');
    /***************************************************** 订单 ****************************************************/
    //列表
    Route::get('order', 'addon\shop\app\api\controller\order\Order@lists');

    //详情
    Route::get('order/:order_id', 'addon\shop\app\api\controller\order\Order@detail');

    //订单状态
    Route::get('order/status', 'addon\shop\app\api\controller\order\Order@orderStatus');

    //创建订单
    Route::post('order_create/create', 'addon\shop\app\api\controller\order\OrderCreate@create');
    //计算
    Route::get('order_create/calculate', 'addon\shop\app\api\controller\order\OrderCreate@calculate');
    //查询优惠券
    Route::get('order_create/coupon', 'addon\shop\app\api\controller\order\OrderCreate@getCoupon');
    // 查询自提点
    Route::get('order_create/store', 'addon\shop\app\api\controller\order\OrderCreate@getStore');

    //获取订单确认数据
    Route::get('confirm', 'addon\shop\app\api\controller\order\OrderCreate@confirm');
    // 订单关闭
    Route::put('order/close/:id', 'addon\shop\app\api\controller\order\Order@orderClose');
    // 订单完成
    Route::put('order/finish/:id', 'addon\shop\app\api\controller\order\Order@orderFinish');
    //物流跟踪
    Route::get('order/logistics', 'addon\shop\app\api\controller\order\Order@getPackage');
    //添加优惠券
    Route::post('coupon', 'addon\shop\app\api\controller\marketing\Coupon@receive');
    //优惠券列表
    Route::get('member/coupon', 'addon\shop\app\api\controller\marketing\Coupon@memberCouponlists');
    //商品收藏列表
    Route::get('goods/collect','addon\shop\app\api\controller\goods\GoodsCollect@getMemberGoodsCollectList');
    //商品添加收藏
    Route::post('goods/collect/:goods_id','addon\shop\app\api\controller\goods\GoodsCollect@addGoodsCollect');
    //商品取消收藏
    Route::delete('goods/collect/:goods_id','addon\shop\app\api\controller\goods\GoodsCollect@cancelGoodsCollect');

    //订单维权 列表
    Route::get('order/refund', 'addon\shop\app\api\controller\refund\Refund@lists');
    //订单维权 详
    Route::get('order/refund/:order_refund_no', 'addon\shop\app\api\controller\refund\Refund@detail');
    // 申请维权
    Route::post('refund/apply', 'addon\shop\app\api\controller\refund\Refund@apply');
    // 修改退款申请
    Route::put('refund/:order_refund_no', 'addon\shop\app\api\controller\refund\Refund@edit');
    // 维权退货
    Route::post('refund/delivery/:order_refund_no', 'addon\shop\app\api\controller\refund\Refund@delivery');
    // 修改维权退货信息
    Route::put('refund/delivery/:order_refund_no', 'addon\shop\app\api\controller\refund\Refund@editDelivery');
    // 取消维权
    Route::put('refund/close/:order_refund_no', 'addon\shop\app\api\controller\refund\Refund@close');
    // 退款原因
    Route::get('refund/reason', 'addon\shop\app\api\controller\refund\Refund@getRefundReason');
    // 退款方式
    Route::get('order/refund/type', 'addon\shop\app\api\controller\refund\Refund@getRefundType');
})->middleware(ApiChannel::class)
    ->middleware(ApiCheckToken::class, true)//表示验证登录
    ->middleware(ApiLog::class);
