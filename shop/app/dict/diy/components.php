<?php

return [
    'SHOP_COMPONENT' => [
        'title' => get_lang('dict_diy.shop_component_type_basic'),
        'list' => [
            'GoodsList' => [
                'title' => '商品列表',
                'icon' => 'iconfont-iconshangpinliebiao',
                'path' => 'edit-goods-list',
                'support_page' => [],
                'uses' => 0,
                'sort' => 10011,
                'value' => [
                    'style' => 'style1',
                    'source' => 'all',
                    'num' => 10,
                    'goods_category' => '',
                    'goods_ids' => []
                ],
            ],
            'ShopSearch' => [
                'title' => '搜索',
                'icon' => 'iconfont-iconsousuo',
                'path' => 'edit-shop-search',
                'support_page' => [],
                'uses' => 1,
                'sort' => 10012,
                'value' => [
                    "searchStyle" => "style-1",
                    "searchLink" => [
                        "name" => ""
                    ],
                    "title" => "搜索",
                    "iconType" => "img",
                    "icon" => "",
                    "imageUrl" => ""
                ],
            ]
        ]
    ],

];