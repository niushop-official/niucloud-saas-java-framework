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

namespace addon\shop\app\service\core\coupon;

use addon\shop\app\dict\coupon\CouponMemberDict;
use addon\shop\app\model\coupon\CouponMember;
use core\base\BaseCoreService;
use core\exception\CommonException;

/**
 * 会员优惠券服务层
 */
class CoreCouponMemberService extends BaseCoreService
{

    public function __construct()
    {
        parent::__construct();
        $this->model = new CouponMember();
    }

    /**
     * 通过会员id查询会员有效可用的优惠券
     * @param $member_id
     * @return void
     */
    public function getUseCouponListByMemberId($member_id)
    {
        $where = array (
            [ 'member_id', '=', $member_id ],
            [ 'status', '=', CouponMemberDict::WAIT_USE ]
        );
        $field = 'id, coupon_id, member_id, create_time, expire_time, use_time, type, status, price, min_condition_money, title';
        return $this->model->where($where)->field($field)->with([
            'goods'
        ])->select()->toArray();
    }

    /**
     * 查询会员优惠券
     * @param $id
     * @return array
     * @throws \think\db\exception\DataNotFoundException
     * @throws \think\db\exception\DbException
     * @throws \think\db\exception\ModelNotFoundException
     */
    public function getUseCouponById($id)
    {
        $where = array (
            [ 'id', '=', $id ],
            [ 'status', '=', CouponMemberDict::WAIT_USE ]
        );
        $field = 'id, coupon_id, member_id, create_time, expire_time, use_time, type, status, price, min_condition_money, title';
        return $this->model->where($where)->field($field)->with([
            'goods'
        ])->findOrEmpty()->toArray();
    }

    /**
     * 恢复(只单个恢复)
     * @return void
     */
    public function recover($id)
    {

    }

    /**
     * 失效
     * @return void
     */
    public function invalid()
    {

    }

    /**
     * 使用
     * @param array $data
     * @return void
     */
    public function use(array $data)
    {
        $id = $data[ 'id' ];
        $where = array (
            [ 'id', '=', $id ],
            [ 'status', '=', CouponMemberDict::WAIT_USE ]
        );
        $coupon = $this->model->where($where)->findOrEmpty();
        if ($coupon->isEmpty()) throw new CommonException('SHOP_COUPON_IS_USED_OR_EXIST');//优惠券不存在或已使用
        $coupon->save(
            [
                'status' => CouponMemberDict::USED,
                'trade_id' => $data[ 'trade_id' ],
                'user_time' => time()
            ]
        );
        return true;
    }

}
