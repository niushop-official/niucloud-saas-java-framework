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

namespace addon\shop\app\adminapi\controller\refund;

use addon\shop\app\service\admin\refund\RefundActionService;
use addon\shop\app\service\admin\refund\RefundService;
use core\base\BaseAdminController;
use think\Response;

class Refund extends BaseAdminController
{
    /**
     * 列表
     * @return Response
     */
    public function lists()
    {
        $data = $this->request->params([
            [ 'order_refund_no', '' ],
            [ 'status', '' ],
            [ 'create_time', [] ],
        ]);
        return success(( new RefundService() )->getPage($data));
    }

    /**
     * 详情
     * @param string $order_refund_no
     * @return Response
     */
    public function detail(string|int $id)
    {
        return success(( new RefundService() )->getDetail($id));
    }

    /**
     * 审核退款
     * @return Response
     */
    public function auditApply(string|int $order_refund_no)
    {
        $data = $this->request->params([
            [ 'shop_reason', '' ],
            [ 'is_agree', ''],
            [ 'money', 0],
            [ 'refund_address_id', 0]
        ]);
        $data['order_refund_no'] = $order_refund_no;
        (new RefundActionService())->auditApply($data);
        return success();
    }

    /**
     * 确认收货
     * @return Response
     */
    public function auditRefundGoods(string|int $order_refund_no)
    {
        $data = $this->request->params([
            [ 'shop_reason', '' ],
            [ 'is_agree', '']
        ]);
        $data['order_refund_no'] = $order_refund_no;
        (new RefundActionService())->auditRefundGoods($data);
        return success();
    }
}
