<?php

return [
    'SHOP_LINK' => [
        'key' => 'shop',
        'addon_title' => get_lang('dict_diy.shop_title'),
        'title' => get_lang('dict_diy.shop_link'),
        'child_list' => [
            [
                'name' => 'SHOP_INDEX',
                'title' => get_lang('dict_diy.shop_link_index'),
                'url' => '/addon/shop/pages/index',
                'is_share' => 1,
                'action' => 'decorate'
            ],
            [
                'name' => 'SHOP_GOODS_CATEGORY',
                'title' => get_lang('dict_diy.shop_link_goods_category'),
                'url' => '/addon/shop/pages/goods/category',
                'is_share' => 1,
                'action' => ''
            ],
            [
                'name' => 'SHOP_GOODS_LIST',
                'title' => get_lang('dict_diy.shop_link_goods_list'),
                'url' => '/addon/shop/pages/goods/list',
                'is_share' => 1,
                'action' => ''
            ],
            [
                'name' => 'SHOP_GOODS_CART',
                'title' => get_lang('dict_diy.shop_link_goods_cart'),
                'url' => '/addon/shop/pages/goods/cart',
                'is_share' => 1,
                'action' => ''
            ],
            [
                'name' => 'SHOP_COUPON_LIST',
                'title' => get_lang('dict_diy.shop_link_coupon_list'),
                'url' => '/addon/shop/pages/coupon/list',
                'is_share' => 1,
                'action' => ''
            ],
            [
                'name' => 'SHOP_MEMBER_INDEX',
                'title' => get_lang('dict_diy.page_link_member_index'),
                'url' => '/addon/shop/pages/member/index',
                'is_share' => 1,
                'action' => 'decorate'
            ],
            [
                'name' => 'SHOP_MY_COUPON',
                'title' => get_lang('dict_diy.shop_link_my_coupon'),
                'url' => '/addon/shop/pages/member/my_coupon',
                'is_share' => 1,
                'action' => ''
            ],
            [
                'name' => 'SHOP_ORDER_LIST',
                'title' => get_lang('dict_diy.shop_link_order_list'),
                'url' => '/addon/shop/pages/order/list',
                'is_share' => 1,
                'action' => ''
            ],
            [
                'name' => 'SHOP_REFUND_LIST',
                'title' => get_lang('dict_diy.shop_link_order_refund_list'),
                'url' => '/addon/shop/pages/refund/list',
                'is_share' => 1,
                'action' => ''
            ],
        ]
    ],
];
