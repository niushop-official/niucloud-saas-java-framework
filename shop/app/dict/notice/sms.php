<?php
return [
    'shop_order_pay_remind' => [
        'content' => '您购买的“{body}”还没付款哦，店家为你预留到{timeout}，再不付款宝贝就被别人买走啦。查看详情{url}',
    ],
    'shop_order_pay' => [
        'content' => '您购买的“{body}”已支付成功。查看详情{url}',
    ],
    'shop_order_delivery' => [
        'content' => '您购买的“{body}”已于{delivery_time}发货。查看详情{url}',
    ],
    'shop_refund_agree' => [
        'content' => '商家已同意您退款编号为{order_refund_no}的退款申请，请关注具体的退款情况',
    ],
    'shop_refund_refuse' => [
        'content' => '很抱歉，您退款编号为{order_refund_no}的退款申请未被同意，请您联系商家并修改申请',
    ],
];
