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

namespace addon\shop\app\api\controller\order;

use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\service\api\order\OrderService;
use core\base\BaseApiController;
use think\Response;

class Order extends BaseApiController
{
    /**
     * 订单列表
     * @return Response
     */
    public function lists()
    {
        $data = $this->request->params([
            [ 'order_no', '' ],
            [ 'status', '' ],
        ]);
        return success(( new OrderService() )->getPage($data));
    }

    /**
     * 订单详情
     * @param int $order_id
     * @return Response
     */
    public function detail(int $order_id)
    {
        return success(( new OrderService() )->getDetail($order_id));
    }

    /**
     * 获取订单状态
     * @return Response
     */
    public function orderStatus()
    {
        return success(OrderDict::getStatus());
    }

    /**
     * 订单关闭
     * @param $id
     * @return Response
     */
    public function orderClose($id){
        return success(( new OrderService() )->close($id));
    }

    /**
     * 订单完成
     * @param $id
     * @return Response
     */
    public function orderFinish($id){
        return success(( new OrderService() )->finish($id));
    }

    public function getPackage()
    {
        $data = $this->request->params([
            [ 'id', '' ],
            [ 'mobile', '' ],
        ]);
        return success(( new OrderService() )->getDeliveryPackage($data));
    }
}
