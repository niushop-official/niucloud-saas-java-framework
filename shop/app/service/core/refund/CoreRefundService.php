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

namespace addon\shop\app\service\core\refund;

use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\dict\order\OrderGoodsDict;
use addon\shop\app\dict\order\OrderLogDict;
use addon\shop\app\dict\order\OrderRefundDict;
use addon\shop\app\model\order\Order;
use addon\shop\app\model\order\OrderGoods;
use addon\shop\app\model\order\OrderRefund;
use app\dict\pay\RefundDict;
use core\base\BaseCoreService;
use core\exception\CommonException;

/**
 * 订单退款服务层
 */
class CoreRefundService extends BaseCoreService
{

    public function __construct()
    {
        parent::__construct();
        $this->model = new OrderRefund();
    }

    /**
     *  查询退款信息
     * @param $order_refund_no
     * @return array
     */
    public function getInfo($order_refund_no)
    {
        //查询订单
        $where = array(
            ['order_refund_no', '=', $order_refund_no]
        );
        return $this->model->where($where)->findOrEmpty()->toArray();
    }
    /**
     * 退款转账完成
     * @param $data
     * @return void
     */
    public function transferSuccess($data)
    {
        $order_refund_no = $data['order_refund_no'];
        $refund_no = $data['refund_no'];
        $update_data = array(
            'transfer_time' => time(),
            'refund_no' => $refund_no
        );
        $this->model->where([
            [
                'order_refund_no', '=', $order_refund_no
            ]
        ])->update($update_data);
        $this->finish([
            'order_refund_no' => $order_refund_no,
            'main_type' => OrderLogDict::SYSTEM,
        ]);
        return true;
    }

    /**
     * 完成
     * @param $data
     * @return void
     */
    public function finish($data)
    {
        $order_refund_no = $data['order_refund_no'];
        //查询订单项信息
        $order_refund_info = $this->model->where([
            ['order_refund_no', '=', $order_refund_no],
        ])->findOrEmpty();
        if ($order_refund_info->isEmpty()) throw new CommonException('SHOP_ORDER_REFUND_IS_NOT_FOUND');//退款已失效
        if ($order_refund_info['status'] == OrderRefundDict::FINISH) throw new CommonException('SHOP_ORDER_REFUND_IS_INVALID_OR_FINISH');//退款已完成或已失效
        $update_data = array(
            'status' => OrderRefundDict::FINISH,
        );
        $order_refund_info->save($update_data);

        //对应的要将订单项设置为已完成
        $order_goods_where = array(
            ['order_refund_no', '=', $order_refund_no]
        );
        $order_goods_update_data = array(
            'status' => OrderGoodsDict::REFUND_FINISH,
            'is_enable_refund' => 0,//禁用退款
        );
        (new OrderGoods())->where($order_goods_where)->update($order_goods_update_data);
        //订单完成退款后事件
        $data['order_refund_no'] = $order_refund_no;
        $data['refund_data'] = array_merge($order_refund_info->toArray(), $update_data);
        event('AfterShopOrderRefundFinish', $data);
        return true;
    }

    /**
     * 待转账
     * @param $order_refund_data
     * @param $main_type
     * @param $main_id
     * @return true
     */
    public function toTransfer($order_refund_data, $main_type, $main_id)
    {
        if ($order_refund_data['money'] > 0) {
            $core_refund_service = new \app\service\core\pay\CoreRefundService();
            $order = (new Order())->where([
                ['order_id', '=', $order_refund_data['order_id']]
            ])->findOrEmpty();
            if ($order->isEmpty()) throw new CommonException('SHOP_ORDER_NOT_FOUND');
            $refund_no = $core_refund_service->create($order_refund_data['site_id'], $order['out_trade_no'], $order_refund_data['money'], get_lang('SHOP_ORDER_BUYER_APPLY_REFUND'), OrderDict::TYPE, $order_refund_data['order_refund_no']);
            $this->model->where(
                [
                    ['order_refund_no', '=', $order_refund_data['order_refund_no']]
                ]
            )->update(
                [
                    'refund_no' => $refund_no,
                    'status' => OrderRefundDict::STORE_REFUND_TRANSFERING
                ]
            );
            $core_refund_service->refund($order_refund_data['site_id'], $refund_no, '', RefundDict::BACK, $main_type, $main_id);
        }
        //状态改变
        return true;
    }
}
