<?php

return [
    'bind' => [

    ],
    'listen' => [
        // 站点创建之后
        'AddSiteAfter' => [ 'addon\shop\app\listener\AddSiteAfterListener' ],
        //订单创建后
        'AfterShopOrderCreate' => [ 'addon\shop\app\listener\order\AfterShopOrderCreate' ],
        //订单支付后
        'AfterShopOrderPay' => [ 'addon\shop\app\listener\order\AfterShopOrderPay' ],
        //订单发货后
        'AfterShopOrderDelivery' => [ 'addon\shop\app\listener\order\AfterShopOrderDelivery' ],
        //订单收货后
        'AfterShopOrderFinish' => [ 'addon\shop\app\listener\order\AfterShopOrderFinish' ],
        //订单关闭后
        'AfterShopOrderClose' => [ 'addon\shop\app\listener\order\AfterShopOrderClose' ],
        /***************************************************** 退款 start *****************************************************/
        'AfterShopOrderRefundApply' => [ 'addon\shop\app\listener\refund\AfterShopOrderRefundApply' ],
        'AfterShopOrderRefundAuditApply' => [ 'addon\shop\app\listener\refund\AfterShopOrderRefundAuditApply' ],
        'AfterShopOrderRefundAuditReturnGoods' => [ 'addon\shop\app\listener\refund\AfterShopOrderRefundAuditReturnGoods' ],
        'AfterShopOrderRefundClose' => [ 'addon\shop\app\listener\refund\AfterShopOrderRefundClose' ],
        'AfterShopOrderRefundDelivery' => [ 'addon\shop\app\listener\refund\AfterShopOrderRefundDelivery' ],
        'AfterShopOrderRefundEdit' => [ 'addon\shop\app\listener\refund\AfterShopOrderRefundEdit' ],
        'AfterShopOrderRefundEditDelivery' => [ 'addon\shop\app\listener\refund\AfterShopOrderRefundEditDelivery' ],
        'AfterShopOrderRefundFinish' => [ 'addon\shop\app\listener\refund\AfterShopOrderRefundFinish' ],
        /***************************************************** 退款 end *****************************************************/

        'ShopPromotion' => [ 'addon\shop\app\listener\app\ShopPromotionListener' ],
        'WapIndex' => [ 'addon\shop\app\listener\WapIndexListener' ],
        'BottomNavigation' => [ 'addon\shop\app\listener\BottomNavigationListener' ],

        //支付
        'PayCreate' => [ 'addon\shop\app\listener\pay\PayCreateListener' ],
        'PaySuccess' => [ 'addon\shop\app\listener\pay\PaySuccessListener' ],
        'RefundSuccess' => [ 'addon\shop\app\listener\pay\RefundSuccessListener' ],

        'NoticeData' => [
            'addon\shop\app\listener\notice_template\OrderPay',
            'addon\shop\app\listener\notice_template\OrderPayRemind',
            'addon\shop\app\listener\notice_template\OrderDelivery',
            'addon\shop\app\listener\notice_template\RefundAgree',
            'addon\shop\app\listener\notice_template\RefundRefuse',
        ],
        //优惠券
        'CouponReceiveType' => [ 'addon\shop\app\listener\coupon\CouponReceiveListener' ],
    ],
    'subscribe' => [
    ],
];
