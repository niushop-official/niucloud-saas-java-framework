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

use app\adminapi\middleware\AdminCheckRole;
use app\adminapi\middleware\AdminCheckToken;
use app\adminapi\middleware\AdminLog;
use think\facade\Route;

/**
 * 商城系统
 */
Route::group('shop', function() {

    /************************************************** 配送相关接口 *****************************************************/
    //物流公司 列表
    Route::get('delivery/company', 'addon\shop\app\adminapi\controller\delivery\Company@lists');

    //物流公司 详情
    Route::get('delivery/company/:id', 'addon\shop\app\adminapi\controller\delivery\Company@info');

    //物流公司 添加
    Route::post('delivery/company', 'addon\shop\app\adminapi\controller\delivery\Company@add');

    //物流公司 编辑
    Route::put('delivery/company/:id', 'addon\shop\app\adminapi\controller\delivery\Company@edit');

    //物流公司 删除
    Route::delete('delivery/company/:id', 'addon\shop\app\adminapi\controller\delivery\Company@del');

    //物流查询接口 设置
    Route::post('delivery/search', 'addon\shop\app\adminapi\controller\delivery\DeliverySearch@setConfig');

    //物流跟踪接口 查询
    Route::get('delivery/search', 'addon\shop\app\adminapi\controller\delivery\DeliverySearch@getConfig');

    //运费模版 分页列表
    Route::get('shipping/template', 'addon\shop\app\adminapi\controller\delivery\ShippingTemplate@pages');

    //运费模版 列表
    Route::get('shipping/template/list', 'addon\shop\app\adminapi\controller\delivery\ShippingTemplate@lists');

    //运费模版 详情
    Route::get('shipping/template/:template_id', 'addon\shop\app\adminapi\controller\delivery\ShippingTemplate@info');

    //运费模版 添加
    Route::post('shipping/template', 'addon\shop\app\adminapi\controller\delivery\ShippingTemplate@add');

    //运费模版 编辑
    Route::put('shipping/template/:template_id', 'addon\shop\app\adminapi\controller\delivery\ShippingTemplate@edit');

    //运费模版 删除
    Route::delete('shipping/template/:template_id', 'addon\shop\app\adminapi\controller\delivery\ShippingTemplate@del');

    //自提门店列表
    Route::get('delivery/store', 'addon\shop\app\adminapi\controller\delivery\Store@lists');

    //自提门店详情
    Route::get('delivery/store/:id', 'addon\shop\app\adminapi\controller\delivery\Store@info');

    //添加自提门店
    Route::post('delivery/store', 'addon\shop\app\adminapi\controller\delivery\Store@add');

    //编辑自提门店
    Route::put('delivery/store/:id', 'addon\shop\app\adminapi\controller\delivery\Store@edit');

    //删除自提门店
    Route::delete('delivery/store/:id', 'addon\shop\app\adminapi\controller\delivery\Store@del');

    //物流配置
    Route::get('delivery/deliveryList', 'addon\shop\app\adminapi\controller\delivery\Delivery@getDeliveryList');
    Route::put('delivery/setConfig', 'addon\shop\app\adminapi\controller\delivery\Delivery@setDeliveryConfig');
    Route::put('delivery/deliverySort', 'addon\shop\app\adminapi\controller\delivery\Delivery@setDeliverySort');

    //配送员列表
    Route::get('delivery/staff', 'addon\shop\app\adminapi\controller\delivery\Delivery@lists');
    //配送员详情
    Route::get('delivery/staff/:id', 'addon\shop\app\adminapi\controller\delivery\Delivery@info');
    //添加配送员
    Route::post('delivery/staff', 'addon\shop\app\adminapi\controller\delivery\Delivery@add');
    //编辑配送员
    Route::put('delivery/staff/:id', 'addon\shop\app\adminapi\controller\delivery\Delivery@edit');
    //删除配送员
    Route::delete('delivery/staff/:id', 'addon\shop\app\adminapi\controller\delivery\Delivery@del');

    // 获取同城配送设置
    Route::get('local', 'addon\shop\app\adminapi\controller\delivery\Local@getLocal');

    // 设置同城配送
    Route::put('local', 'addon\shop\app\adminapi\controller\delivery\Local@setLocal');

    /************************************************** 接口管理 *******************************************************/

    //商品分页列表
    Route::get('goods', 'addon\shop\app\adminapi\controller\goods\Goods@pages');

    //商品详情
    Route::get('goods/:id', 'addon\shop\app\adminapi\controller\goods\Goods@info');

    //添加实物商品
    Route::post('goods', 'addon\shop\app\adminapi\controller\goods\Goods@add');

    //编辑实物商品
    Route::put('goods/:id', 'addon\shop\app\adminapi\controller\goods\Goods@edit');

    // 商品添加/编辑数据
    Route::get('goods/init', 'addon\shop\app\adminapi\controller\goods\Goods@init');

    //添加虚拟商品
    Route::post('goods/virtual', 'addon\shop\app\adminapi\controller\goods\VirtualGoods@add');

    //编辑虚拟商品
    Route::put('goods/virtual/:id', 'addon\shop\app\adminapi\controller\goods\VirtualGoods@edit');

    // 商品添加/编辑数据
    Route::get('goods/virtual/init', 'addon\shop\app\adminapi\controller\goods\VirtualGoods@init');

    //删除商品
    Route::put('goods/delete', 'addon\shop\app\adminapi\controller\goods\Goods@del');

    // 回收站商品分页列表
    Route::get('goods/recycle', 'addon\shop\app\adminapi\controller\goods\Goods@recyclePages');

    //商品恢复
    Route::put('goods/recycle', 'addon\shop\app\adminapi\controller\goods\Goods@recycle');

    // 修改商品排序号
    Route::put('goods/sort', 'addon\shop\app\adminapi\controller\goods\Goods@editSort');

    // 修改商品上下架状态
    Route::put('goods/status', 'addon\shop\app\adminapi\controller\goods\Goods@editStatus');

    // 复制商品
    Route::put('goods/copy/:goods_id', 'addon\shop\app\adminapi\controller\goods\Goods@copy');

    // 获取商品选择分页列表
    Route::get('goods/select', 'addon\shop\app\adminapi\controller\goods\Goods@select');

    // 获取商品SKU规格列表
    Route::get('goods/sku', 'addon\shop\app\adminapi\controller\goods\Goods@sku');

    // 编辑商品规格列表库存
    Route::put('goods/sku/stock', 'addon\shop\app\adminapi\controller\goods\Goods@editGoodsListStock');

    // 编辑商品规格列表价格
    Route::put('goods/sku/price', 'addon\shop\app\adminapi\controller\goods\Goods@editGoodsListPrice');

    // 获取商品类型
    Route::get('goods/type', 'addon\shop\app\adminapi\controller\goods\Goods@type');

    //商品标签分页列表
    Route::get('goods/label', 'addon\shop\app\adminapi\controller\goods\Label@pages');

    //商品标签列表
    Route::get('goods/label/list', 'addon\shop\app\adminapi\controller\goods\Label@lists');

    //商品标签详情
    Route::get('goods/label/:id', 'addon\shop\app\adminapi\controller\goods\Label@info');

    //添加商品标签
    Route::post('goods/label', 'addon\shop\app\adminapi\controller\goods\Label@add');

    //编辑商品标签
    Route::put('goods/label/:id', 'addon\shop\app\adminapi\controller\goods\Label@edit');

    //删除商品标签
    Route::delete('goods/label/:id', 'addon\shop\app\adminapi\controller\goods\Label@del');

    //商品品牌分页列表
    Route::get('goods/brand', 'addon\shop\app\adminapi\controller\goods\Brand@pages');

    //商品品牌列表
    Route::get('goods/brand/list', 'addon\shop\app\adminapi\controller\goods\Brand@lists');

    //商品品牌详情
    Route::get('goods/brand/:id', 'addon\shop\app\adminapi\controller\goods\Brand@info');

    //添加商品品牌
    Route::post('goods/brand', 'addon\shop\app\adminapi\controller\goods\Brand@add');

    //编辑商品品牌
    Route::put('goods/brand/:id', 'addon\shop\app\adminapi\controller\goods\Brand@edit');

    //删除商品品牌
    Route::delete('goods/brand/:id', 'addon\shop\app\adminapi\controller\goods\Brand@del');

    //商品服务分页列表
    Route::get('goods/service', 'addon\shop\app\adminapi\controller\goods\Service@pages');

    //商品服务列表
    Route::get('goods/service/list', 'addon\shop\app\adminapi\controller\goods\Service@lists');

    //商品服务详情
    Route::get('goods/service/:id', 'addon\shop\app\adminapi\controller\goods\Service@info');

    //添加商品服务
    Route::post('goods/service', 'addon\shop\app\adminapi\controller\goods\Service@add');

    //编辑商品服务
    Route::put('goods/service/:id', 'addon\shop\app\adminapi\controller\goods\Service@edit');

    //删除商品服务
    Route::delete('goods/service/:id', 'addon\shop\app\adminapi\controller\goods\Service@del');

    //商品分类列表树结构
    Route::get('goods/tree', 'addon\shop\app\adminapi\controller\goods\Category@tree');

    Route::get('goods/category', 'addon\shop\app\adminapi\controller\goods\Category@lists');

    //商品分类详情
    Route::get('goods/category/:id', 'addon\shop\app\adminapi\controller\goods\Category@info');

    //添加商品分类
    Route::post('goods/category', 'addon\shop\app\adminapi\controller\goods\Category@add');

    //编辑商品分类
    Route::put('goods/category/:id', 'addon\shop\app\adminapi\controller\goods\Category@edit');

    //删除商品分类
    Route::delete('goods/category/:id', 'addon\shop\app\adminapi\controller\goods\Category@del');

    //编辑商品分类
    Route::post('goods/category/update', 'addon\shop\app\adminapi\controller\goods\Category@editCategory');

    // 获取商品分类配置
    Route::post('goods/category/config', 'addon\shop\app\adminapi\controller\goods\Category@setGoodsCategoryConfig');

    // 获取商品分类配置
    Route::get('goods/category/config', 'addon\shop\app\adminapi\controller\goods\Category@getGoodsCategoryConfig');

    // 获取商品分类树结构供弹框调用
    Route::get('goods/category/components', 'addon\shop\app\adminapi\controller\goods\Category@components');

    /************************************************** 订单相关接口 *****************************************************/
    //交易配置
    Route::post('order/config', 'addon\shop\app\adminapi\controller\order\Config@setConfig');
    Route::get('order/config', 'addon\shop\app\adminapi\controller\order\Config@getConfig');

    //订单列表
    Route::get('order/list', 'addon\shop\app\adminapi\controller\order\Order@lists');
    //订单详情
    Route::get('order/detail/:id', 'addon\shop\app\adminapi\controller\order\Order@detail');
    //获取 订单类型
    Route::get('order/type', 'addon\shop\app\adminapi\controller\order\Order@getOrderType');
    //获取 订单状态
    Route::get('order/status', 'addon\shop\app\adminapi\controller\order\Order@getOrderStatus');
    //订单关闭
    Route::put('order/close/:id', 'addon\shop\app\adminapi\controller\order\Order@orderClose');
    //订单发货
    Route::put('order/delivery', 'addon\shop\app\adminapi\controller\order\Order@orderDelivery');
    //订单项发货
    Route::put('order/goods/delivery/:id', 'addon\shop\app\adminapi\controller\order\Order@orderDelivery');
    //获取订单配送方式
    Route::get('order/delivery_type', 'addon\shop\app\adminapi\controller\order\Order@getDeliveryType');
    //商家留言
    Route::put('order/shop_remark', 'addon\shop\app\adminapi\controller\order\Order@setShopRemark');
    //订单完成
    Route::put('order/finish/:id', 'addon\shop\app\adminapi\controller\order\Order@orderFinish');
    //获取 物流包裹信息
    Route::get('order/delivery/package', 'addon\shop\app\adminapi\controller\order\Order@getOrderPackage');
    //获取 支付类型
    Route::get('order/pay/type', 'addon\shop\app\adminapi\controller\order\Order@getPayType');
    //获取 订单来源
    Route::get('order/from', 'addon\shop\app\adminapi\controller\order\Order@getOrderFrom');

    //订单维权 列表
    Route::get('order/refund', 'addon\shop\app\adminapi\controller\refund\Refund@lists');
    //订单维权 详情
    Route::get('order/refund/:id', 'addon\shop\app\adminapi\controller\refund\Refund@detail');
    //订单维权审核
    Route::put('order/refund/audit/:order_refund_no', 'addon\shop\app\adminapi\controller\refund\Refund@auditApply');
    //订单维权审核
    Route::put('order/refund/delivery/:order_refund_no', 'addon\shop\app\adminapi\controller\refund\Refund@auditRefundGoods');

    //营销中心
    Route::get('marketing', 'addon\shop\app\adminapi\controller\marketing\Index@index');
    /************************************************** 优惠券相关接口 *****************************************************/
    //优惠券列表
    Route::get('goods/coupon', 'addon\shop\app\adminapi\controller\marketing\Coupon@lists');
    //优惠券初始化信息
    Route::get('goods/coupon/init', 'addon\shop\app\adminapi\controller\marketing\Coupon@init');
    //添加优惠券
    Route::post('goods/coupon', 'addon\shop\app\adminapi\controller\marketing\Coupon@add');
    //优惠券领取记录
    Route::get('goods/coupon/records', 'addon\shop\app\adminapi\controller\marketing\Coupon@getMemberCoupon');
    //优惠券详情
    Route::get('goods/coupon/detail/:id', 'addon\shop\app\adminapi\controller\marketing\Coupon@info');
    //编辑优惠券
    Route::put('goods/coupon/edit/:id', 'addon\shop\app\adminapi\controller\marketing\Coupon@edit');
    //优惠券设置状态
    Route::put('goods/coupon/setstatus/:status', 'addon\shop\app\adminapi\controller\marketing\Coupon@setCouponStatus');
    //删除优惠券
    Route::delete('goods/coupon/:id', 'addon\shop\app\adminapi\controller\marketing\Coupon@del');
    //商家地址库列表
    Route::get('shop_address', 'addon\shop\app\adminapi\controller\shop_address\ShopAddress@lists');
    //商家地址库详情
    Route::get('shop_address/:id', 'addon\shop\app\adminapi\controller\shop_address\ShopAddress@info');
    //添加商家地址库
    Route::post('shop_address', 'addon\shop\app\adminapi\controller\shop_address\ShopAddress@add');
    //编辑商家地址库
    Route::put('shop_address/:id', 'addon\shop\app\adminapi\controller\shop_address\ShopAddress@edit');
    //删除商家地址库
    Route::delete('shop_address/:id', 'addon\shop\app\adminapi\controller\shop_address\ShopAddress@del');
    // 默认发货地址
    Route::get('shop_address/default/delivery', 'addon\shop\app\adminapi\controller\shop_address\ShopAddress@defaultDelivery');
    //获取商家收货地址库
    Route::get('order/refund/address', 'addon\shop\app\adminapi\controller\shop_address\ShopAddress@getList');

    //商品评价 列表
    Route::get('goods/evaluate', 'addon\shop\app\adminapi\controller\goods\Evaluate@lists');
    //商品评价 添加
    Route::post('goods/evaluate', 'addon\shop\app\adminapi\controller\goods\Evaluate@add');
    //商品评价 删除
    Route::delete('goods/evaluate/:id', 'addon\shop\app\adminapi\controller\goods\Evaluate@del');
    //商品评价 回复
    Route::put('goods/evaluate/reply/:id', 'addon\shop\app\adminapi\controller\goods\Evaluate@evaluateReply');
    //商品评价 通过
    Route::put('goods/evaluate/adopt/:id', 'addon\shop\app\adminapi\controller\goods\Evaluate@adopt');
    //商品评价 拒绝
    Route::put('goods/evaluate/refuse/:id', 'addon\shop\app\adminapi\controller\goods\Evaluate@refuse');
    //商品评价 置顶
    Route::put('goods/evaluate/topping/:id', 'addon\shop\app\adminapi\controller\goods\Evaluate@topping');
    //商品评价 取消置顶
    Route::put('goods/evaluate/cancel_topping/:id', 'addon\shop\app\adminapi\controller\goods\Evaluate@cancelTopping');

    Route::get('stat/total', 'addon\shop\app\adminapi\controller\Stat@total');
    Route::get('stat/today', 'addon\shop\app\adminapi\controller\Stat@today');
    Route::get('stat/yesterday', 'addon\shop\app\adminapi\controller\Stat@yesterday');
    Route::get('stat', 'addon\shop\app\adminapi\controller\Stat@stat');
    Route::get('stat/order', 'addon\shop\app\adminapi\controller\Stat@order');
    Route::get('stat/goods', 'addon\shop\app\adminapi\controller\Stat@goods');

    // 发票列表
    Route::get('invoice', 'addon\shop\app\adminapi\controller\order\Invoice@lists');
    // 发票信息
    Route::get('invoice/:id', 'addon\shop\app\adminapi\controller\order\Invoice@info');
    // 开票
    Route::put('invoice/:id', 'addon\shop\app\adminapi\controller\order\Invoice@invoicing');
})->middleware([
    AdminCheckToken::class,
    AdminCheckRole::class,
    AdminLog::class
]);
