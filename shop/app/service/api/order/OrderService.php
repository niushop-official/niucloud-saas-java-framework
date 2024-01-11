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

namespace addon\shop\app\service\api\order;

use addon\shop\app\dict\delivery\DeliveryDict;
use addon\shop\app\dict\order\OrderDeliveryDict;
use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\dict\order\OrderLogDict;
use addon\shop\app\model\delivery\Store;
use addon\shop\app\model\order\Order;
use addon\shop\app\model\order\OrderDelivery;
use addon\shop\app\service\core\order\CoreOrderCloseService;
use addon\shop\app\service\core\order\CoreOrderFinishService;
use addon\shop\app\service\core\order\CoreOrderService;
use app\model\pay\Pay;
use core\base\BaseApiService;

/**
 *  订单服务层
 */
class OrderService extends BaseApiService
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
        $field = 'order_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,create_time,pay_time,delivery_type,taker_name,taker_mobile,taker_full_address,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,pay_money,is_evaluate';
        $order = 'create_time desc';
        $search_model = $this->model
            ->where([ ['site_id', '=', $this->site_id],[ 'member_id', '=', $this->member_id ] ])
            ->withSearch([ 'order_no', 'status' ], $where)
            ->field($field)
            ->with(
                [
                    'order_goods' => function($query) {
                        $query->field('order_goods_id, site_id, order_id, member_id, goods_id, sku_id, goods_name, sku_name, goods_image, sku_image, price, num, goods_money, is_enable_refund')->append([ 'goods_image_thumb_small' ]);
                    }
                ]
            )->order($order)->append([ 'order_from_name', 'order_type_name', 'status_name', 'delivery_type_name' ]);
        $order_status_list = OrderDict::getStatus();
        $list = $this->pageQuery($search_model, function($item, $key) use ($order_status_list) {
            $item[ 'order_status_data' ] = $order_status_list[ $item[ 'status' ] ] ?? [];
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
        $field = 'order_id,site_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,invoice_id,create_time,pay_time,delivery_time,take_time,finish_time,close_time,delivery_type,taker_name,taker_mobile,taker_province,taker_city,taker_district,taker_address,taker_full_address,taker_longitude,taker_latitude,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,discount_money,is_evaluate';
        $info = $this->model->where([ ['site_id', '=', $this->site_id], [ 'order_id', '=', $order_id ], [ 'member_id', '=', $this->member_id ] ])->field($field)
            ->with(
                [
                    'order_goods' => function($query) {
                        $query->field('order_goods_id, site_id, order_id, member_id, goods_id, sku_id, goods_name, sku_name, goods_image, sku_image, price, num, goods_money, discount_money, is_enable_refund, status, order_refund_no, delivery_status')->append([ 'goods_image_thumb_small' ]);
                    }
                ]
            )->append([ 'order_from_name', 'order_type_name', 'status_name', 'delivery_type_name' ])->findOrEmpty()->toArray();
        if (!empty($info)) {
            $info[ 'order_status_data' ] = $order_status_list[ $info[ 'status' ] ] ?? [];

            if ($info[ 'delivery_type' ] == DeliveryDict::STORE) {
                $info[ 'store' ] = ( new Store() )->where([ [ 'store_id', '=', $info[ 'take_store_id' ] ] ])
                    ->field('store_id, store_name, store_mobile, store_logo, trade_time, longitude, latitude, full_address')
                    ->findOrEmpty()->toArray();
            }

            if ($info[ 'out_trade_no' ]) {
                $info[ 'pay' ] = ( new Pay() )->where([ [ 'out_trade_no', '=', $info[ 'out_trade_no' ] ] ])
                    ->field('out_trade_no, type, pay_time')->append([ 'type_name' ])
                    ->findOrEmpty()->toArray();
            }

            if ($info[ 'delivery_type' ] == DeliveryDict::EXPRESS) {
                $info[ 'order_delivery' ] = ( new OrderDelivery() )
                    ->where([ [ 'order_id', '=', $info[ 'order_id' ] ] ])
                    ->field('id, order_id, name, delivery_type, express_company_id, sub_delivery_type, express_number, create_time')
                    ->select()->toArray();
            }
        }
        return $info;
    }


    /**
     * 订单关闭
     * @param array $data
     * @return void
     */
    public function close(int $order_id)
    {
        $data[ 'main_type' ] = OrderLogDict::MEMBER;
        $data[ 'main_id' ] = $this->member_id;
        $data[ 'close_type' ] = OrderDict::SHOP_CLOSE;
        $data[ 'order_id' ] = $order_id;
        $data[ 'site_id' ] = $this->site_id;
        ( new CoreOrderCloseService() )->close($data);
        return true;
    }


    /**
     * 订单收货
     * @param $order
     * @return void
     */
    public function finish($order_id)
    {
        $data = [];
        $data[ 'order_id' ] = $order_id;
        $data[ 'main_type' ] = OrderLogDict::MEMBER;
        $data[ 'main_id' ] = $this->member_id;
        $data[ 'site_id' ] = $this->site_id;
        ( new CoreOrderFinishService() )->finish($data);
        return true;
    }

    /**
     * 物流信息
     * @param $id
     */
    public function getDeliveryPackage($data)
    {
        $field = 'id, order_id, site_id, name, delivery_type, sub_delivery_type, express_company_id, express_number, local_deliver_id, status, create_time';
        $info = ( new OrderDelivery() )->where([ [ 'id', '=', $data[ 'id' ] ], ['site_id', '=', $this->site_id] ])->with([
            'company' => function($query) {
                $query->field('company_id, company_name, express_no');
            },
            'order_goods' => function($query) {
                $query->field('goods_name, sku_name, site_id, goods_image, delivery_id, num, price')->append([ 'goods_image_thumb_small' ]);
            }
        ])->field($field)->findOrEmpty()->toArray();

        if (!empty($info) && $info['delivery_type'] == OrderDeliveryDict::EXPRESS && $info['sub_delivery_type'] != OrderDeliveryDict::NONE_EXPRESS) {
            $info['mobile'] = $data['mobile'];
            $info = (new CoreOrderService())->deliverySearch($info);
            return $info;
        }
        return $info;
    }
}
