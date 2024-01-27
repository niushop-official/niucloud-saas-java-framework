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

use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\dict\order\OrderGoodsDict;
use addon\shop\app\dict\order\OrderLogDict;
use addon\shop\app\model\order\Order;
use addon\shop\app\model\order\OrderGoods;
use app\service\core\pay\CorePayService;
use core\base\BaseCoreService;
use core\exception\CommonException;

/**
 * 订单关闭服务层
 */
class CoreOrderCloseService extends BaseCoreService
{

    public function __construct()
    {
        parent::__construct();
        $this->model = new Order();
    }

    /**
     * 订单关闭
     * @param array $data
     * @return void
     */
    public function close(array $data)
    {
        $order_data = $this->model->where([
            ['order_id', '=', $data['order_id']],
            ['site_id', '=', $data['site_id']]
        ])->findOrEmpty()->toArray();
        if (empty($order_data)) throw new CommonException('SHOP_ORDER_NOT_FOUND');//订单不存在
        if ($order_data['status'] == OrderDict::CLOSE) throw new CommonException('SHOP_ORDER_IS_CLOSED');
        if($data['close_type'] != OrderDict::REFUND_CLOSE){
            //关闭相关的支付
            (new CorePayService())->closeByTrade($data['site_id'], OrderDict::TYPE, $order_data['order_id']);
        }

        //关闭订单
        $this->model->where([
            [
                ['order_id', '=', $order_data['order_id']]
            ]
        ])->update(
            [
                'status' => OrderDict::CLOSE,
                'close_type' => $data['close_type'],
                'close_remark' => $data['close_remark'] ?? '',
                'is_enable_refund' => 0,
                'timeout' => 0
            ]
        );
        $data['order_data'] = $order_data;
//        //订单关闭后操作
//        event('AfterShopOrderClose', $data);

        //订单关闭操作
        CoreOrderEventService::orderClose($data);
        //订单关闭后操作
        CoreOrderEventService::orderCloseAfter($data);
        return true;
    }

    /**
     * 校验一下是否全部退款
     * @param $data
     * @return void
     * @throws \think\db\exception\DbException
     */
    public function checkAllClose($data){
        $order_id = $data['order_id'];
        $site_id = $data['site_id'];
        //检测一下订单下的订单项是否全部退款完毕
        $where = array(
            ['order_id', '=', $order_id],
            ['status', '<>', OrderGoodsDict::REFUND_FINISH]
        );
        if((new OrderGoods())->where($where)->count() == 0){
            $data = [];
            $data['main_type'] = OrderLogDict::SYSTEM;
            $data['main_id'] = 0;
            $data['close_type'] = OrderDict::REFUND_CLOSE;
            $data['order_id'] = $order_id;
            $data['site_id'] = $site_id;
            $this->close($data);
        }
        return true;
    }

}
