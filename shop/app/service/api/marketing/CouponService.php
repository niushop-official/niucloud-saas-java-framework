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

namespace addon\shop\app\service\api\marketing;


use addon\shop\app\dict\coupon\CouponDict;
use addon\shop\app\dict\coupon\CouponMemberDict;
use addon\shop\app\model\coupon\Coupon;
use addon\shop\app\model\coupon\CouponGoods;
use addon\shop\app\model\coupon\CouponMember;
use app\model\member\Member;
use core\base\BaseApiService;
use core\exception\CommonException;
use think\facade\Db;

/**
 *  优惠券服务层
 */
class CouponService extends BaseApiService
{
    public function __construct()
    {
        parent::__construct();
        $this->model = new Coupon();
    }

    /**
     * 获取优惠券列表
     * @return array
     */
    public function getPage($data)
    {
        $goods_coupon_id = [];
        if ($data[ 'goods_id' ] != '') {
            $coupon_goods_model = new CouponGoods();
            $goods_coupon_list = $coupon_goods_model->where([ ['site_id', '=', $this->site_id], [ 'goods_id', '=', $data[ 'goods_id' ] ] ])->select()->toArray();
            if (!empty($goods_coupon)) {
                $goods_coupon_id = array_column($goods_coupon_list, 'coupon_id');
            } else {
                $goods_coupon_id = [];
            }
        }
        $category_coupon_id = [];
        if ($data[ 'category_ids' ] != '') {
            $coupon_goods_model = new CouponGoods();
            $category_coupon_list = $coupon_goods_model->where([ ['site_id', '=', $this->site_id], [ 'category_id', 'in', $data[ 'category_ids' ] ] ])->select()->toArray();
            if (!empty($category_coupon_list)) {
                $category_coupon_id = array_column($category_coupon_list, 'coupon_id');
            } else {
                $category_coupon_id = [];
            }
        }
        $field = 'id,title,start_time,end_time,remain_count,receive_count,limit_count,status,price,min_condition_money,type
        ,receive_type,valid_type,length,valid_start_time,valid_end_time,sort';
        $order = 'id desc';

        $where[] = [ 'site_id', '=', $this->site_id ];
        $where[] = [ 'status', '=', CouponDict::ALL ];

        if (!empty($category_coupon_id) || !empty($goods_coupon_id)) {
            $coupon_ids = array_unique(array_merge($goods_coupon_id, $category_coupon_id));
            $where[] = [ 'coupon_id', 'in', $coupon_ids ];
            $whereOr[] = [ 'type', '=', CouponDict::ALL ];
            $whereOr[] = [ 'start_time', '>', time() ];
            $whereOr[] = [ 'end_time', '<', time() ];
            $search_model = $this->model
                ->field($field)
                ->where($where)
                ->whereOr($whereOr)
                ->order($order)
                ->append([ 'coupon_price', 'coupon_min_price', 'receive_type_name', 'type_name' ]);
        } else {

            $search_model = $this->model
                ->field($field)
                ->where($where)
                ->order($order)
                ->append([ 'coupon_price', 'coupon_min_price', 'receive_type_name', 'type_name' ]);
        }
        $list = $this->pageQuery($search_model);
        $coupon_member = new CouponMember();
        foreach ($list[ 'data' ] as $k => &$v) {
            if ($v[ 'remain_count' ] != '-1') {
                $v[ 'sum_count' ] = $v[ 'remain_count' ] + $v[ 'receive_count' ];
            } else {
                $v[ 'sum_count' ] = '-1';
            }

            $member_info = ( new Member() )->where([ [ 'site_id', '=', $this->site_id ], [ 'member_id', '=', $this->member_id ] ])->field('member_id')->findOrEmpty()->toArray();
            if ($member_info) {

                $coupon_member_count = $coupon_member->where([ [ 'site_id', '=', $this->site_id ],[ 'member_id', '=', $this->member_id ], [ 'coupon_id', '=', $v[ 'id' ] ] ])->count();
                if ($coupon_member_count) {
                    $v[ 'is_receive' ] = 1;
                    $v[ 'member_receive_count' ] = $coupon_member_count;
                } else {
                    $v[ 'is_receive' ] = 0;
                    $v[ 'member_receive_count' ] = 0;
                }
            } else {
                $v[ 'member_receive_count' ] = 0;
                $v[ 'is_receive' ] = 0;
            }
        }
        return $list;
    }

    /**
     * 查询优惠券详情
     */
    public function getDetail($id)
    {
        $info = $this->model->where([ [ 'id', '=', $id ], [ 'site_id', '=', $this->site_id ] ])->append([ 'coupon_price', 'coupon_min_price' ])->findOrEmpty()->toArray();

        if (empty($info)) {
            throw new CommonException('COUPON_NOT_EXIST');
        }
        if ($info[ 'remain_count' ] != '-1') {
            $info[ 'sum_count' ] = $info[ 'remain_count' ] + $info[ 'receive_count' ];
        } else {
            $info[ 'sum_count' ] = '-1';
        }
        $coupon_member = new CouponMember();
        $member_info = ( new Member() )->where([ [ 'member_id', '=', $this->member_id ], [ 'site_id', '=', $this->site_id ] ])->field('member_id')->findOrEmpty()->toArray();

        if ($member_info) {
            $coupon_member_count = $coupon_member->where([ [ 'member_id', '=', $this->member_id ], [ 'site_id', '=', $this->site_id ], [ 'coupon_id', '=', $id ] ])->count();
            if ($coupon_member_count) {
                $info[ 'is_receive' ] = 1;
                $info[ 'member_receive_count' ] = $coupon_member_count;
            } else {
                $info[ 'is_receive' ] = 0;
                $info[ 'member_receive_count' ] = 0;
            }
        } else {
            $info[ 'is_receive' ] = 0;
            $info[ 'member_receive_count' ] = 0;
        }

        return $info;
    }

    /**
     * 优惠券领取
     */
    public function receive($data)
    {
        $member_id = $this->member_id;
        $coupon_id = $data[ 'coupon_id' ];
        $number = $data[ 'number' ];
        $type = $data[ 'type' ];
        $coupon_member_model = new CouponMember();
        Db::startTrans();
        try {
            //判断是否符合设置的领取方式
            $receive_type = $this->getReceiveType();
            if (!in_array($type, $receive_type)) {
                throw new CommonException('COUPON_RECEIVE_TYPE_NOT_EXIST');
            }
            //判断是否已经领取过
            $member_coupon_count = $coupon_member_model->where([ [ 'coupon_id', '=', $coupon_id ], [ 'site_id', '=', $this->site_id ], [ 'member_id', '=', $member_id ] ])->count();
            //判断优惠券数量是否充足
            $info = $this->model->where([ [ 'id', '=', $coupon_id ], [ 'site_id', '=', $this->site_id ] ])->findOrEmpty()->toArray();
            if (empty($info)) {
                throw new CommonException('COUPON_NOT_EXIST');
            }
            if ($member_coupon_count == $info[ 'limit_count' ]) {
                throw new CommonException('COUPON_RECEIVE_EXCESS');//领取超过可领取数量
            }

            if ($info[ 'remain_count' ] != '-1' && $info[ 'remain_count' ] == 0) {
                throw new CommonException('COUPON_STOCK_INSUFFICIENT');//优惠券已被领完
            }
            $time = time();
            if ($info[ 'start_time' ] > 1) {
                if ($time < $info[ 'start_time' ]) {
                    throw new CommonException('COUPON_RECEIVE_NOT_TIME');//优惠券已被领完
                }
                if ($time > $info[ 'end_time' ]) {
                    throw new CommonException('COUPON_RECEIVE_NOT_TIME');//优惠券已被领完
                }
            }

            if ($info[ 'remain_count' ] != -1) {
                $coupon_data = [
                    'remain_count' => $info[ 'remain_count' ] - $number,
                    'receive_count' => $info[ 'receive_count' ] + $number,
                ];
            } else {
                $coupon_data = [
                    'receive_count' => $info[ 'receive_count' ] + $number,
                ];
            }

            if ($info[ 'valid_type' ] == 1) {
                $expire_time = 86400 * $info[ 'length' ] + time();
            } else {
                $expire_time = $info[ 'valid_end_time' ];
            }
            $member_coupon_data = [
                'site_id' => $this->site_id,
                'coupon_id' => $coupon_id,
                'member_id' => $member_id,
                'create_time' => time(),
                'expire_time' => $expire_time,
                'receive_type' => $type,
                'type' => $info[ 'type' ],
                'title' => $info[ 'title' ],
                'price' => $info[ 'price' ],
                'status' => CouponMemberDict::WAIT_USE,
                'min_condition_money' => $info['min_condition_money']
            ];
            $this->model->where([ [ 'id', '=', $coupon_id ] ])->update($coupon_data);
            $coupon_member_model->create($member_coupon_data);
            Db::commit();
            return true;
        } catch (\Exception $e) {
            Db::rollback();
            throw new CommonException($e->getMessage());
        }

    }

    /**
     * 会员优惠券领取记录
     */
    public function getMemberPage($data)
    {
        if (!empty($data[ 'status' ])) {
            $where[] = [ 'status', '=', $data[ 'status' ] ];
        }
        $where[] = [ 'member_id', '=', $this->member_id ];
        $coupon_member_model = new CouponMember();
        $search_model = $coupon_member_model
            ->where($where)
            ->order([ 'id desc' ])
            ->append([ 'coupon_price', 'coupon_min_price', 'receive_type_name', 'type_name' ]);
        $list = $this->pageQuery($search_model);
        return $list;
    }

    //获取优惠券领取方式
    public function getReceiveType()
    {
        $data = event('CouponReceiveType', []);
        if (empty($data)) {
            return [];
        }
        foreach ($data as &$value) {
            foreach ($value as $v) {
                $type[] = $v[ 'name' ];

            }
        }
        return $type;
    }
}
