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

namespace addon\shop\app\service\core\order;

use addon\shop\app\dict\order\OrderDeliveryDict;
use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\dict\order\OrderGoodsDict;
use addon\shop\app\model\order\Order;
use addon\shop\app\model\order\OrderGoods;
use core\base\BaseCoreService;
use core\exception\CommonException;

/**
 *  订单完成服务层
 */
class CoreOrderFinishService extends BaseCoreService
{

    public function __construct()
    {
        parent::__construct();
        $this->model = new Order();
    }


    /**
     * 订单完成
     * @param $data
     * @return true
     */
    public function finish(array $data)
    {
        $order_id = $data['order_id'];
        $site_id = $data['site_id'];

        //查询订单
        $where = array(
            ['order_id', '=', $order_id],
            ['site_id', '=', $site_id]
        );
        $order = $this->model->where($where)->findOrEmpty()->toArray();
        if (empty($order)) throw new CommonException('SHOP_ORDER_NOT_FOUND');//订单不存在
        if ($order['status'] != OrderDict::WAIT_TAKE) throw new CommonException('SHOP_ONLY_WAIT_TAKE_CAN_BE_TAKE');//只有待收货的订单才可以收货

        //存在退款中的订单项,订单就不可以收货
        $refunding_order_goods_count = (new OrderGoods())->where([
            ['order_id', '=', $order_id],
            ['status', '=', OrderGoodsDict::REFUNDING]
        ])->count();
        if ($refunding_order_goods_count > 0) throw new CommonException('SHOP_ORDER_HAS_REFUNDING_NOT_ALLOW_FINISH');//是否存在退款中的订单项
        $update_data = array(
            'status' => OrderDict::FINISH,
            'finish_time' => time(),
            'timeout' => 0
        );
        $this->model->where($where)->update($update_data);
        //订单项收货
        $this->orderGoodsTake($data);
        $data['order_data'] = array_merge($order, $update_data);
//        event('AfterShopOrderFinish', $data);
        //订单完成操作
        CoreOrderEventService::orderFinish($data);
        //订单完成后操作
        CoreOrderEventService::orderFinishAfter($data);
        return true;
    }

    /**
     * 订单项收货
     * @param $data
     * @return void
     */
    public function orderGoodsTake($data)
    {
        $order_id = $data['order_id'];
        //将待收货的订单项设置已收货
        $order_goods_where = array(
            ['order_id', '=', $order_id],
            ['status', '=', OrderGoodsDict::NORMAL],
//            ['delivery_status', '=', OrderDeliveryDict::DELIVERY_FINISH]
        );
        $order_goods_data = array(
            'delivery_status' => OrderDeliveryDict::TAKED
        );
        (new OrderGoods())->where($order_goods_where)->update($order_goods_data);
        return true;
    }
}
