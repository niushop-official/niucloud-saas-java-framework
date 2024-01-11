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

namespace addon\shop\app\service\admin\order;

use addon\shop\app\dict\delivery\DeliveryDict;
use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\dict\order\OrderGoodsDict;
use addon\shop\app\model\delivery\Store;
use addon\shop\app\model\order\Order;
use addon\shop\app\model\order\OrderDelivery;
use addon\shop\app\model\order\OrderGoods;
use app\model\pay\Pay;
use core\base\BaseAdminService;
use think\db\exception\DbException;
use think\db\Query;

/**
 * 订单
 */
class OrderService extends BaseAdminService
{
    public function __construct()
    {
        parent::__construct();
        $this->model = new Order();
    }

    /**
     * 分页列表
     * @param array $where
     * @return array
     */
    public function getPage(array $where)
    {
        $field = 'order_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,create_time,pay_time,delivery_type,taker_name,taker_mobile,taker_full_address,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,pay_money';
        $order = 'create_time desc';

        $pay_where = [];
        if($where[ 'pay_type' ]){
            $pay_where[] = ['pay.type', '=',  $where[ 'pay_type' ] ];
        }
        $search_model = $this->model
            ->where([ ['order.site_id', '=', $this->site_id ] ])
            ->withSearch([ 'search_type', 'order_from', 'join_status', 'create_time', 'join_pay_time' ], $where)
            ->field($field)
            ->withJoin([
                'pay' => function(Query $query) use($pay_where){
                    $query->where($pay_where);
                },
            ], 'left')
            ->with([
                'order_goods' => function ($query) {
                    $query->field('order_goods_id, order_id, member_id, goods_id, sku_id, goods_name, sku_name, goods_image, sku_image, price, num, goods_money, is_enable_refund, goods_type, delivery_status, status')->append(['delivery_status_name', 'status_name']);
                },
                'member' => function($query) {
                    $query->field('member_id, nickname, mobile, headimg');
                }
            ])->order($order)->append([ 'order_from_name', 'order_type_name', 'status_name', 'delivery_type_name' ]);
        $order_status_list = OrderDict::getStatus();
        $list = $this->pageQuery($search_model, function($item, $key) use ($order_status_list) {
            $item[ 'order_status_data' ] = $order_status_list[ $item[ 'status' ] ] ?? [];
            $item_pay = $item['pay'];
            if(!empty($item_pay)){
                $item_pay->append(['type_name']);
            }
        });
        return $list;
    }

    /**
     * 详情
     * @param int $order_id
     * @return array
     */
    public function getDetail(int $order_id)
    {
        $field = 'order_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,invoice_id,create_time,pay_time,delivery_time,take_time,finish_time,close_time,delivery_type,taker_name,taker_mobile,taker_province,taker_city,taker_district,taker_address,taker_full_address,taker_longitude,taker_latitude,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,discount_money';
        $info = $this->model->where([ [ 'order_id', '=', $order_id ], ['site_id', '=', $this->site_id] ])->field($field)
            ->with(
                [
                    'order_goods' => function($query) {
                        $query->field('order_goods_id, order_id, member_id, goods_id, sku_id, goods_name, sku_name, goods_image, sku_image, price, num, goods_money, is_enable_refund, goods_type, delivery_status, status')->append([ 'delivery_status_name' ]);
                    },
                    'member' => function($query) {
                        $query->field('member_id, nickname, mobile, headimg');
                    },
                    'order_log' => function($query) {
                        $query->field('order_id, content, main_type, create_time, main_id, type')->order("create_time desc")->append([ 'main_type_name', 'type_name', 'main_name' ]);
                    }
                ])->append([ 'order_from_name', 'order_type_name', 'status_name', 'delivery_type_name' ])->findOrEmpty()->toArray();
        $order_status_list = OrderDict::getStatus();
        if (!empty($info)) $info[ 'order_status_data' ] = $order_status_list[ $info[ 'status' ] ] ?? [];

        if ($info[ 'delivery_type' ] == DeliveryDict::STORE) {
            $info[ 'store' ] = ( new Store() )->where([ [ 'store_id', '=', $info[ 'take_store_id' ] ] ])
                ->field('store_id, store_name, full_address, store_mobile, trade_time')
                ->findOrEmpty()->toArray();
        }

        if ($info[ 'delivery_type' ] == DeliveryDict::EXPRESS) {
            $info[ 'order_delivery' ] = ( new OrderDelivery() )
                ->where([ [ 'order_id', '=', $info[ 'order_id' ] ] ])
                ->field('id, order_id, name, delivery_type, express_company_id, express_number, create_time')
                ->select()->toArray();
        }

        if ($info[ 'out_trade_no' ]) {
            $info[ 'pay' ] = ( new Pay() )->where([ [ 'out_trade_no', '=', $info[ 'out_trade_no' ] ] ])
                ->field('out_trade_no, type, pay_time')->append([ 'type_name' ])->findOrEmpty()->toArray();
        }

        return $info;
    }

    /**
     * 商家留言
     * @param $data
     * @return bool
     */
    public function shopRemark($data)
    {
        $this->model->where([ [ 'order_id', '=', $data[ 'order_id' ] ], ['site_id', '=', $this->site_id ] ])->update([ 'shop_remark' => $data[ 'shop_remark' ] ]);
        return true;
    }

    /**
     * 订单数量统计
     * @throws DbException
     */
    public function getOrderCount()
    {
        $data = [
            "wait_pay_order" => 0, //待付款
            "wait_delivery_order" => 0, //待发货
            "wait_take_order" => 0, //待收货
            "refund_order" => 0, //退款中（订单项）
        ];

        $data[ 'wait_pay_order' ] = $this->model->where([ ['site_id', '=', $this->site_id ],[ 'status', '=', OrderDict::WAIT_PAY ] ])->count();
        $data[ 'wait_delivery_order' ] = $this->model->where([ ['site_id', '=', $this->site_id ],[ 'status', '=', OrderDict::WAIT_DELIVERY ] ])->count();
        $data[ 'wait_take_order' ] = $this->model->where([ ['site_id', '=', $this->site_id ],[ 'status', '=', OrderDict::WAIT_TAKE ] ])->count();
        $data[ 'refund_order' ] = ( new OrderGoods() )->where([ ['site_id', '=', $this->site_id ],[ 'status', '=', OrderGoodsDict::REFUNDING ] ])->count();

        return $data;
    }
}
