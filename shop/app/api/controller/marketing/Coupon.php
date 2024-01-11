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

namespace addon\shop\app\api\controller\marketing;

use addon\shop\app\service\api\marketing\CouponService;
use core\base\BaseApiController;
use think\Response;

class Coupon extends BaseApiController
{
    /**
     * 优惠券列表
     * @return Response
     */
    public function lists()
    {
        $data = $this->request->params([
            [ 'goods_id', '' ],
            [ 'category_ids', '' ],
        ]);
        return success(( new CouponService() )->getPage($data));
    }

    /**
     * 优惠券详情
     * @param int $id
     * @return Response
     */
    public function detail(int $id)
    {
        return success(( new CouponService() )->getDetail($id));
    }

    /**
     * 优惠券领取
     */
    public function receive()
    {
        $data = $this->request->params([
            [ 'coupon_id', '' ],
            [ 'type', 'receive' ],
            [ 'number', 1 ],
        ]);
        ( new CouponService())->receive($data);
        return success('COUPON_RECEIVE_SUCCESS');

    }

    /**
     * 会员已领取优惠券列表
     * @return Response
     */
    public function memberCouponlists()
    {
        $data = $this->request->params([
            ['status','']
        ]);
        return success(( new CouponService() )->getMemberPage($data));
    }

}
