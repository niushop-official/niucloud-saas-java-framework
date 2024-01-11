<?php

return [
    [
        'key' => 'shop_order_close',
        'name' => '未支付订单自动关闭',
        'desc' => '',
        'time' => [
            'type' => 'min',
            'min' => 1
        ],
        'class' => 'addon\shop\app\job\order\OrderClose',
        'function' => ''
    ],
    [
        'key' => 'shop_order_close_allow_refund',
        'name' => '订单完成自动关闭售后',
        'desc' => '',
        'time' => [
            'type' => 'min',
            'min' => 1
        ],
        'class' => 'addon\shop\app\job\order\OrderCloseAllowRefund',
        'function' => ''
    ],
    [
    'key' => 'shop_order_finish',
    'name' => '待收货订单自动完成',
    'desc' => '',
    'time' => [
        'type' => 'min',
        'min' => 1
    ],
    'class' => 'addon\shop\app\job\order\OrderFinish',
    'function' => ''
]
];
