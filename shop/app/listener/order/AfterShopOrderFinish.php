<?php
declare (strict_types = 1);

namespace addon\shop\app\listener\order;

use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\model\order\Order;
use addon\shop\app\model\order\OrderGoods;
use addon\shop\app\service\core\order\CoreOrderConfigService;
use addon\shop\app\service\core\order\CoreOrderLogService;

class AfterShopOrderFinish
{

    public function handle($data){
        $order_data = $data['order_data'];


        //日志
        $main_type = $data['main_type'];
        $main_id = $data['main_id'];
        (new CoreOrderLogService())->add([
            'order_id' => $order_data['order_id'],
            'status' => OrderDict::FINISH,
            'main_type' => $main_type,
            'main_id' => $main_id,
            'type' => OrderDict::ORDER_FINISH_ACTION,
            'content' => ''
        ]);
        //消息发送

        //定时关闭订单允许退款开关
        $core_order_config_service = new CoreOrderConfigService();
        $order_config = $core_order_config_service->orderRefund($order_data['site_id']);
        if($order_config['no_allow_refund'] == 1){
            (new OrderGoods())->where([['order_id', '=', $order_data['order_id']]])->update([
                'is_enable_refund' => 0
            ]);
        } else {
            if($order_config['refund_length'] > 0){
                (new Order())->where([['order_id', '=', $order_data['order_id']]])->update([
                    'timeout' => $order_data['finish_time'] + $order_config['refund_length'] * 60
                ]);
//                OrderCloseAllowRefund::dispatch(['order_id' => $order_data['order_id'] ], secs: $order_config['refund_length'] * 86400);
            }
        }
    }
}
