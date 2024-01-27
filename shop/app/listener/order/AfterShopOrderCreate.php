<?php
declare (strict_types = 1);

namespace addon\shop\app\listener\order;

use addon\shop\app\dict\order\InvoiceDict;
use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\dict\order\OrderLogDict;
use addon\shop\app\job\order\OrderClose;
use addon\shop\app\job\order\OrderPayRemind;
use addon\shop\app\model\order\Order;
use addon\shop\app\model\order\OrderGoods;
use addon\shop\app\service\core\cart\CoreCartService;
use addon\shop\app\service\core\CoreStatService;
use addon\shop\app\service\core\goods\CoreGoodsSaleNumService;
use addon\shop\app\service\core\goods\CoreGoodsStockService;
use addon\shop\app\service\core\order\CoreInvoiceService;
use addon\shop\app\service\core\order\CoreOrderConfigService;
use addon\shop\app\service\core\order\CoreOrderLogService;
use core\exception\CommonException;
use think\facade\Db;

class AfterShopOrderCreate
{

    public function handle($data)
    {
        $basic = $data['basic'];
        $order_data = $data['order_data'];
        $order_goods_data = $data['order_goods_data'] ?? [];
        $site_id = $order_data['site_id'];
        Db::startTrans();
        try{
            //减去商品库存
            //循环商品项扣除库存

            $core_goods_stock_service = new CoreGoodsStockService();
            foreach($order_goods_data as $v){
                $core_goods_stock_service->dec([
                    'num' => $v['num'],
                    'goods_id' => $v['goods_id'],
                    'sku_id' => $v['sku_id']
                ]);
            }
            Db::commit();
        } catch (\Exception $e) {
            Db::rollback();
            throw new CommonException($e->getMessage());
        }

        //购物车删除
        $cart_ids = $data['cart_ids'] ?? [];
        if($cart_ids){
            (new CoreCartService())->deleteByCartIds($cart_ids);
        }

        //累增销量
        $core_goods_sale_num_service = new CoreGoodsSaleNumService();
        foreach($order_goods_data as $v){
            //商品累计销量
            $core_goods_sale_num_service->inc([
                'num' => $v['num'],
                'goods_id' => $v['goods_id'],
                'sku_id' => $v['sku_id']
            ]);
        }

        //写入发票
        $invoice = $basic['invoice'];
        if(!empty($invoice)){
            $invoice_id = (new CoreInvoiceService())->add([
                'site_id' => $site_id,
                'type' => $invoice['type'] ?? '',
                'name' => $invoice['name'],
                'header_type' => $invoice['header_type'],
                'header_name' => $invoice['header_name'],
                'tax_number' => $invoice['tax_number'],
                'mobile' => $invoice['mobile'],
                'email' => $invoice['email'],
                'telephone' => $invoice['telephone'],
                'address' => $invoice['address'],
                'bank_name' => $invoice['bank_name'],
                'bank_card_number' => $invoice['bank_card_number'],
                'money' => $order_data['order_money'],
                'status' => InvoiceDict::WAIT_OPEN,
                'member_id' => $order_data['member_id'],
                'trade_type' => $order_data['order_type'],
                'trade_id' => $order_data['order_id'],
            ]);
            //修改订单发票id
            (new Order())->where([
                ['order_id', '=', $order_data['order_id']]
            ])->update(['invoice_id' => $invoice_id]);
        }

        //发布日志
        $main_type = $data['main_type'] ?? OrderLogDict::MEMBER;
        $main_id = $data['main_id'] ?? $order_data['member_id'];
        (new CoreOrderLogService())->add([
            'order_id' => $order_data['order_id'],
            'status' => OrderDict::WAIT_PAY,
            'main_type' => $main_type,//todo  可以是传入的
            'main_id' => $main_id,
            'type' => OrderDict::ORDER_CREATE_ACTION,
            'content' => ''
        ]);

        //消息发送

        //创建定时关闭任务
        $core_order_config_service = new CoreOrderConfigService();
        $order_config = $core_order_config_service->orderClose($site_id);
        if($order_config['is_close'] == 1){
            if($order_config['close_length'] > 0){
                (new Order())->where([['order_id', '=', $order_data['order_id']]])->update([
                    'timeout' => $data['time'] + $order_config['close_length'] * 60
                ]);
//                OrderClose::dispatch(['order_id' => $order_data['order_id'] ], secs: $order_config['close_length'] * 60);
            }
        }
        //增加统计数据
        CoreStatService::addStat(['site_id' => $site_id, 'order_num' => 1]);
        // 订单催付通知
        OrderPayRemind::dispatch(['site_id' => $site_id, 'order_id' => $order_data['order_id'] ], secs:1800);
    }
}
