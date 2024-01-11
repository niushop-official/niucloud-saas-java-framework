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

namespace addon\shop\app\adminapi\controller\order;

use addon\shop\app\dict\order\OrderDeliveryDict;
use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\service\admin\order\OrderFinishService;
use addon\shop\app\service\admin\order\OrderService;
use addon\shop\app\service\admin\order\OrderCloseService;
use addon\shop\app\service\admin\order\OrderDeliveryService;
use app\dict\common\ChannelDict;
use app\dict\pay\PayDict;
use core\base\BaseAdminController;
use think\Response;

class Order extends BaseAdminController
{
    /**
     * 订单列表
     * @return Response
     */
    public function lists()
    {
        $data = $this->request->params([
            ['search_type', ''],
            ['search_name', ''],
            ['status', ''],
            ['pay_type', ''],
            ['order_from', ''],
            ['create_time', []],
            ['pay_time', []],
        ]);
        return success((new OrderService())->getPage($data));
    }

    /**
     * 订单详情
     * @param int $order_id
     * @return Response
     */
    public function detail(int $id)
    {
        return success((new OrderService())->getDetail($id));
    }

    /**
     * 获取订单状态
     * @return Response
     */
    public function getOrderStatus()
    {
        return success(OrderDict::getStatus());
    }

    /**
     * 获取订单类型
     * @return Response
     */
    public function getOrderType()
    {
        return success(OrderDict::getOrderType());
    }

    /**
     * 订单关闭
     * @param $id
     * @return Response
     */
    public function orderClose($id)
    {
        return success((new OrderCloseService())->close($id));
    }

    /**
     * 订单完成
     * @param $id
     * @return Response
     */
    public function orderFinish($id)
    {
        (new OrderFinishService())->finish($id);
        return success();
    }
    /**
     * 订单发货
     * @param $id
     * @return Response
     */
    public function orderDelivery()
    {
        $data = $this->request->params([
            ['order_id', 0],
            ['order_goods_ids', []],
            ['delivery_type', ''],
            ['express_company_id', ''],
            ['express_number', ''],
            ['local_deliver_id', 0],//配送员
            ['remark', ''],//配送员
        ]);
        return success("DELIVERY_SUCCESS", (new OrderDeliveryService())->delivery($data));
    }

    /**
     * 获取订单配送方式
     */
    public function getDeliveryType()
    {
        $data = $this->request->params([
            ['delivery_type', ''],
        ]);
        return success(OrderDeliveryDict::getChildType($data['delivery_type']));
    }

    /**
     * 商家留言
     * @return Response
     */
    public function setShopRemark()
    {
        $data = $this->request->params([
            ['order_id', ''],
            ['shop_remark', ''],
        ]);
        (new OrderService())->shopRemark($data);
        return success("SUCCESS");
    }

    /**
     * 订单包裹
     * @return Response
     */
    public function getOrderPackage(){
        $data = $this->request->params([
            [ 'id', '' ],
            [ 'mobile', '' ],
        ]);
        return success(( new OrderDeliveryService() )->getDeliveryPackage($data));
    }

    /**
     * 获取支付方式
     * @return Response
     */
    public function getPayType()
    {
        return success(PayDict::getPayType());
    }

    /**
     * 获取订单来源
     */
    public function getOrderFrom()
    {
        return success(ChannelDict::getType());
    }
}
