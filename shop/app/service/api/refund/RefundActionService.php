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

namespace addon\shop\app\service\api\refund;

use addon\shop\app\dict\order\OrderDeliveryDict;
use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\dict\order\OrderGoodsDict;
use addon\shop\app\dict\order\OrderRefundDict;
use addon\shop\app\dict\order\OrderRefundLogDict;
use addon\shop\app\model\order\OrderGoods;
use addon\shop\app\model\order\OrderRefund;
use addon\shop\app\service\core\refund\CoreRefundActionService;
use core\base\BaseApiService;
use core\exception\ApiException;

/**
 *  订单服务层
 */
class RefundActionService extends BaseApiService
{
    public function __construct()
    {
        parent::__construct();
        $this->model = new OrderRefund();
    }

    /**
     * 退款申请
     * @param $data
     * @return void
     */
    public function apply($data)
    {
        $order_goods_id = $data['order_goods_id'];
        //查询订单项信息
        $order_goods_info = (new OrderGoods())->where([
            ['order_goods_id', '=', $order_goods_id],
            ['member_id', '=', $this->member_id]
        ])->findOrEmpty();
        if ($order_goods_info->isEmpty()) throw new ApiException('SHOP_ORDER_IS_INVALID');//订单已失效
        if(!$order_goods_info['is_enable_refund']) throw new ApiException('SHOP_ORDER_IS_NOT_ENABLE_REFUND');//订单不允许退款
        //查询有没有正没有关闭的退款
        if ($order_goods_info['status'] != OrderGoodsDict::NORMAL) throw new ApiException('SHOP_ORDER_REFUND_IS_REFUND_FINISH');//订单已退款或存在未完成的退款
        $order_refund_no = create_no();
        $refund_type = $data['refund_type'];
        //只有已发货,才能退货退款
        if($refund_type == OrderRefundDict::RETURN_AND_REFUND_GOODS && $order_goods_info['delivery_status'] == OrderDeliveryDict::WAIT_DELIVERY)  throw new ApiException('SHOP_ORDER_REFUND_DELIVERY_NOT_ALLOW_REFUND_GOODS');
        $apply_money = $data['apply_money'];
        //退款金额不能大于可退款总额
        if ($apply_money > ($order_goods_info['goods_money'] - $order_goods_info['discount_money'])) throw new ApiException('SHOP_ORDER_REFUND_MONEY_GT_ORDER_MONEY');//退款金额不能大于可退款总额
        $reason = $data['reason'];
        $insert_data = array(
            'order_id' => $order_goods_info['order_id'],
            'site_id' => $this->site_id,
            'order_goods_id' => $order_goods_id,
            'order_refund_no' => $order_refund_no,
            'refund_type' => $refund_type,
            'reason' => $reason,
            'member_id' => $this->member_id,
            'apply_money' => $apply_money,
            'status' => OrderRefundDict::BUYER_APPLY_WAIT_STORE,
            'remark' => $data['remark'],
            'voucher' => $data['voucher'],
            'source' => OrderRefundDict::APPLY
        );
        $this->model->create($insert_data);

        //将订单项的退款单号覆盖
        $order_goods_info->save([
            'order_refund_no' => $order_refund_no,
            'status' => OrderGoodsDict::REFUNDING
        ]);
        //订单申请退款后事件
        event('AfterShopOrderRefundApply', ['order_refund_no' => $order_refund_no, 'refund_data' => $insert_data]);
        return true;
    }

    /**
     * 维权
     * @param $data
     * @return true
     */
    public function edit($data)
    {
        $order_refund_no = $data['order_refund_no'];
        //查询订单项信息
        $order_refund_info = $this->model->where([
            ['order_refund_no', '=', $order_refund_no],
            ['member_id', '=', $this->member_id],
            ['site_id', '=', $this->site_id]
        ])->findOrEmpty();
        if ($order_refund_info->isEmpty()) throw new ApiException('SHOP_ORDER_IS_INVALID');//退款已失效
        if ($order_refund_info['status'] != OrderRefundDict::BUYER_APPLY_WAIT_STORE) throw new ApiException('SHOP_ORDER_IS_INVALID');//退款已失效(只有被拒绝的请求才可以修改退款)
        $refund_type = $data['refund_type'];
        $apply_money = $data['apply_money'];


        $order_goods_id = $order_refund_info['order_goods_id'];
        //查询订单项信息
        $order_goods_info = (new OrderGoods())->where([
            ['order_goods_id', '=', $order_goods_id],
            ['member_id', '=', $this->member_id]
        ])->findOrEmpty();
        if ($order_goods_info->isEmpty()) throw new ApiException('SHOP_ORDER_REFUND_IS_INVALID');//订单已失效
        //退款金额不能大于可退款总额
        if ($apply_money > ($order_goods_info['goods_money'] - $order_goods_info['discount_money'])) throw new ApiException('SHOP_ORDER_REFUND_MONEY_GT_ORDER_MONEY');//退款金额不能大于可退款总额
        $reason = $data['reason'];
        $update_data = [
            'refund_type' => $refund_type,
            'reason' => $reason,
            'apply_money' => $apply_money,
            'status' => OrderRefundDict::BUYER_APPLY_WAIT_STORE,
            'remark' => $data['remark'],
            'voucher' => $data['voucher'],
            'source' => OrderRefundDict::APPLY
        ];
        $order_refund_info->save($update_data);
        //订单申请退款后事件
        event('AfterShopOrderRefundEdit', ['site_id' => $this->site_id, 'order_refund_no' => $order_refund_no, 'refund_data' => array_merge($order_refund_info->toArray(), $update_data)]);
        return true;
    }

    /**
     * 退款关闭
     * @param $data
     * @return void
     */
    public function close($data)
    {
        $data[ 'main_type' ] = OrderRefundLogDict::MEMBER;
        $data[ 'main_id' ] = $this->member_id;
        (new CoreRefundActionService())->close($data);
        return true;
    }

    /**
     * 退款发货
     * @param $data
     * @return void
     */
    public function delivery($data)
    {
        $order_refund_no = $data['order_refund_no'];
        //查询订单项信息
        $order_refund_info = $this->model->where([
            ['order_refund_no', '=', $order_refund_no],
            ['member_id', '=', $this->member_id],
            ['site_id', '=', $this->site_id]
        ])->findOrEmpty();
        if ($order_refund_info->isEmpty()) throw new ApiException('SHOP_ORDER_REFUND_IS_INVALID');//退款已失效
        if ($order_refund_info['status'] != OrderRefundDict::STORE_AGREE_REFUND_GOODS_APPLY_WAIT_BUYER) throw new ApiException('SHOP_ORDER_REFUND_STATUS_NOT_SUPPORT_ACTION');//退款已失效(只有被拒绝的请求才可以修改退货)

        $order_goods_id = $order_refund_info['order_goods_id'];
        //查询订单项信息
        $order_goods_info = (new OrderGoods())->where([
            ['order_goods_id', '=', $order_goods_id],
            ['member_id', '=', $this->member_id]
        ])->findOrEmpty();
        if ($order_goods_info->isEmpty()) throw new ApiException('SHOP_ORDER_REFUND_IS_INVALID');//订单已失效
        //todo 配送信息增加
        $delivery_param = $data['delivery'];
        $delivery = array(
            'express_number' => $delivery_param['express_number'],
            'express_company' => $delivery_param['express_company'],
            'remark' => $delivery_param['remark'],
        );
        $update_data = [
            'delivery' => $delivery,
            'status' => OrderRefundDict::BUYER_REFUND_GOODS_WAIT_STORE,
        ];
        $order_refund_info->save($update_data);
        //订单申请退款后事件
        event('AfterShopOrderRefundDelivery', ['site_id' => $this->site_id, 'main_type' => OrderRefundLogDict::MEMBER, 'main_id' => $this->member_id, 'order_refund_no' => $order_refund_no, 'refund_data' => array_merge($order_refund_info->toArray(), $update_data)]);
        return true;
    }

    /**
     * 修改发货信息
     * @param $data
     * @return true
     */
    public function editDelivery($data)
    {
        $order_refund_no = $data['order_refund_no'];
        //查询订单项信息
        $order_refund_info = $this->model->where([
            ['order_refund_no', '=', $order_refund_no],
            ['member_id', '=', $this->member_id],
            ['site_id', '=', $this->site_id]
        ])->findOrEmpty();
        if ($order_refund_info->isEmpty()) throw new ApiException('SHOP_ORDER_REFUND_IS_INVALID');//退款已失效
        if ($order_refund_info['status'] != OrderRefundDict::STORE_REFUSE_TAKE_REFUND_GOODS_WAIT_BUYER) throw new ApiException('SHOP_ORDER_REFUND_STATUS_NOT_SUPPORT_ACTION');//退款已失效(只有被拒绝的请求才可以修改退款)

        $order_goods_id = $order_refund_info['order_goods_id'];
        //查询订单项信息
        $order_goods_info = (new OrderGoods())->where([
            ['order_goods_id', '=', $order_goods_id],
            ['member_id', '=', $this->member_id]
        ])->findOrEmpty();
        if ($order_goods_info->isEmpty()) throw new ApiException('SHOP_ORDER_IS_INVALID');//订单已失效
        //todo 配送信息增加
        $delivery_param = $data['delivery'];
        $delivery = array(
            'express_number' => $delivery_param['express_number'],
            'express_company' => $delivery_param['express_company'],
            'remark' => $delivery_param['remark'],
        );
        $update_data = [
            'delivery' => $delivery,
            'status' => OrderRefundDict::BUYER_REFUND_GOODS_WAIT_STORE,
        ];
        $order_refund_info->save($update_data);
        //订单申请退款后事件
        event('AfterShopOrderRefundEditDelivery', ['main_type' => OrderRefundLogDict::MEMBER, 'site_id' => $this->site_id, 'main_id' => $this->member_id, 'order_refund_no' => $order_refund_no, 'refund_data' => array_merge($order_refund_info->toArray(), $update_data)]);
        return true;
    }

    /**
     *
     * @return array
     */
    public function getRefundReason()
    {
        return OrderRefundDict::getRefundReason();
    }

}
