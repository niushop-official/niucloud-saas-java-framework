<?php

return [
    'dict_shop_delivery' => [
        'num' => '按件',
        'weight' => '按重量',
        'volume' => '按体积'
    ],
    'dict_shop_goods' => [
        'real' => '实物商品',
        'real_desc' => '物流发货',
        'virtual' => '虚拟商品',
        'virtual_desc' => '无需物流'
    ],
    'dict_shop_app_manage' => [
        'coupon' => '优惠券',
        'marketing' => '营销活动',
    ],
    'dict_shop_order' => [
        'status_wait_pay' => '待支付',
        'status_wait_shipping' => '待发货',
        'status_wait_take' => '待收货',
        'status_finish' => '已完成',
        'status_close' => '已关闭'
    ],
    'dict_shop_delivery_type' => [
        'express' => '物流配送',
        'local_delivery' => '同城配送',
        'store' => '门店自提',
        'virtual' => '虚拟发货',
        'none_express' => '无需物流'
    ],
    'dict_shop_coupon' => [
        'user' => '手动领取',
        'grant' => '后台或活动发放',
        'all' => '通用券',
        'category' => '品类券',
        'goods' => '商品券',
        'wait_start' => '未开始',
        'normal' => '进行中',
        'expire' => '已过期',
        'invalid' => '已失效',
    ],
    'dict_shop_member_coupon' => [
        'wait_use' => '未使用',
        'used' => '已使用',
        'expire' => '已过期',
        'invalid' => '已失效',
        'send' => '后台发放',
        'receive' => '用户手动领取'
    ],
    'dict_wap_index' => [
        'shop' => '商城',
        'shop_desc' => '商品购买，配送'
    ],
    'dict_shop_order_log' => [
        'store' => '商家',
        'member' => '会员',
        'system' => '系统'
    ],
    'dict_shop_goods_evalute' => [
        'audit' => '待审核',
        'audit_adopt' => '审核通过',
        'audit_refuse' => '审核拒绝'
    ],
    'dict_shop_order_action' => [
        'order_create_action' => '订单创建',
        'order_pay_action' => '订单支付',
        'order_close_action' => '订单关闭',
        'order_delivery_action' => '订单发货',
        'order_finish_action' => '订单完成',
        'order_remark_action' => '订单商家备注',
        'order_close_allow_refund' => '订单自动关闭售后',
    ],
    'dict_shop_invoice' => [
        'header_type_company' => '企业',
        'header_type_person' => '个人',
        'common' => '普票',
        'sprcial' => '专票'
    ],
    'dict_shop_order_refund_status' => [
        'buyer_apply_wait_store' => '买家申请售后',
        'store_agree_refund_goods_apply_wait_buyer' => '商家同意售后',
        'store_refuse_refund_goods_apply_wait_buyer' => '商家拒绝售后',
        'buyer_refund_goods_wait_store' => '买家已退货',
        'store_refuse_take_refund_goods_wait_buyer' => '商家拒绝收货',
        'store_agree_refund_wait_transfer' => '同意售后申请',
        'store_refund_transfering' => '转账中',
        'finish' => '退款成功',
        'close' => '退款关闭'
    ],
    'dict_diy' => [
        'shop_component_type_basic' => '商城组件',
        'page_shop_index' => '商城首页',
        'page_shop_member_index' => '商城个人中心',

        'shop_title' => '商城',
        'shop_link' => '商城链接',
        'shop_link_index' => '商城首页',
        'shop_link_goods_category' => '商品分类',
        'shop_link_goods_list' => '商品列表',
        'shop_link_goods_cart' => '购物车',
        'page_link_member_index' => '商城个人中心',
        'shop_link_coupon_list' => '优惠券列表',
        'shop_link_my_coupon' => '我的优惠券',
        'shop_link_order_list' => '订单列表',
        'shop_link_order_refund_list' => '退款列表'
    ],
    'dict_shop_delivery_status' => [
        'wait_delivery' => '待发货',
        'delivery' => '配送中',
        'delivery_finish' => '已发货',
        'taked' => '已收货'
    ],
    'dict_shop_order_refund_action' => [
        'apply' => '买家申请退款',
        'edit_apply' => '买家修改退款',
        'delivery' => '买家退货',
        'edit_delivery' => '买家修改退货',
        'agree_audit_apply' => '卖家同意退款',
        'refuse_audit_apply_action' => '卖家同意退货',
        'agree_audit_refund_goods' => '卖家同意退货',
        'refuse_audit_refund_goods' => '卖家拒绝退货',
        'active_refund' => '卖家主动退款',
        'finish' => '退款完成',
        'close' => '关闭退款'
    ],

    'dict_shop_order_refund_reason' => [
        'delivery_timeout' => '未按约定时间发货',
        'buy_more_or_dislike' => '拍错/多拍/不喜欢',
        'negotiation_completed' => '协商一致退款',
        'other' => '其他',
    ],


    'dict_shop_order_refund_type' => [
        'only_refund' => '仅退款',
        'return_and_refund_goods' => '退款退货'
    ],
    'dict_shop_order_refund' => [
        'normal' => '正常',
        'refunding' => '退款中',
        'refund_finish' => '退款完成'
    ]
];
