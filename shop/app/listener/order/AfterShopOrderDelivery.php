<?php
declare (strict_types = 1);

namespace addon\shop\app\listener\order;

//发货后事件
use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\dict\order\OrderLogDict;
use addon\shop\app\model\order\Order;
use addon\shop\app\service\core\order\CoreOrderConfigService;
use addon\shop\app\service\core\order\CoreOrderLogService;
use app\service\core\notice\NoticeService;

class AfterShopOrderDelivery
{

    public function handle($data){

        //日志
        $order_data = $data['order_data'];
        $main_type = $data['main_type'] ?? OrderLogDict::SYSTEM;
        $main_id = $data['main_id'] ?? 0;
        (new CoreOrderLogService())->add([
            'order_id' => $order_data['order_id'],
            'status' => OrderDict::WAIT_TAKE,
            'main_type' => $main_type,
            'main_id' => $main_id,
            'type' => OrderDict::ORDER_DELIVERY_ACTION,
            'content' => ''
        ]);
        //消息发送
        (new NoticeService())->send($order_data['site_id'], 'shop_order_delivery', ['order_id' => $data['order_id'] ]);

        //写入定时收货任务
        $core_order_config_service = new CoreOrderConfigService();
        $order_config = $core_order_config_service->orderConfirm($order_data['site_id']);
        if($order_config['is_finish'] == 1){
            if($order_config['finish_length'] > 0){
                (new Order())->where([['order_id', '=', $order_data['order_id']]])->update([
                    'timeout' => strtotime($order_data['delivery_time']) + $order_config['finish_length'] * 86400
                ]);
//                OrderFinish::dispatch(['order_id' => $order_data['order_id'] ], secs: $order_config['finish_length'] * 86400);
            }
        }
    }
}
