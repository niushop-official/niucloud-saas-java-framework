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

namespace addon\shop\app\service\admin\marketing;

use addon\shop\app\model\coupon\Coupon;
use addon\shop\app\model\coupon\CouponGoods;
use addon\shop\app\model\coupon\CouponMember;
use addon\shop\app\service\admin\goods\CategoryService;
use core\exception\AdminException;
use core\base\BaseAdminService;
use core\exception\CommonException;
use think\facade\Db;

/**
 * 优惠券服务层
 * Class StoreService
 * @package addon\shop\app\service\admin\delivery
 */
class CouponService extends BaseAdminService
{
    public function __construct()
    {
        parent::__construct();
        $this->model = new Coupon();
    }

    public function getInit()
    {
        // 查询商品分类
        $goods_category_tree = [];
        $goods_category_service = new CategoryService();
        $goods_category = $goods_category_service->getTree();
        foreach ($goods_category as $k => $v) {
            $children = [];
            if (!empty($v[ 'child_list' ])) {
                foreach ($v[ 'child_list' ] as $ck => $cv) {
                    $children[] = [
                        'value' => $cv[ 'category_id' ],
                        'label' => $cv[ 'category_name' ],
                    ];
                }
            }
            $goods_category_tree[] = [
                'value' => $v[ 'category_id' ],
                'label' => $v[ 'category_name' ],
                'children' => $children
            ];
        }
        $res[ 'goods_category_tree' ] = $goods_category_tree;
        return $res;
    }

    /**
     * 获取优惠券列表
     * @param array $where
     * @return array
     */
    public function getPage(array $where = [])
    {
        $field = 'id,title,price,type,receive_type,start_time,end_time,remain_count,receive_count,status,limit_count,min_condition_money,receive_status,valid_type,length,valid_end_time';
        $order = 'id desc';
        $search_model = $this->model->where([ ['site_id', '=', $this->site_id] ])->withSearch([ "title", "status" ], $where)->append([ 'type_name', 'receive_type_name', 'status_name' ])->field($field)->order($order);
        $list = $this->pageQuery($search_model);
        foreach ($list[ 'data' ] as $k => &$v) {
            if ($v[ 'remain_count' ] != '-1') {
                $v[ 'sum_count' ] = $v[ 'remain_count' ] + $v[ 'receive_count' ];
            } else {
                $v[ 'sum_count' ] = '-1';
            }
            //查询已使用数量
            $coupon_member_model = new CouponMember();
            $v[ 'receive_use_count' ] = $coupon_member_model->where([ [ 'coupon_id', '=', $v[ 'id' ] ],['site_id', '=', $this->site_id], [ 'use_time', '>', 1 ] ])->count();
        }
        return $list;
    }

    /**
     * 获取优惠券详情
     * @param int $id
     * @return array
     */
    public function getInfo(int $id)
    {
        $info = $this->model->where([ [ 'id', '=', $id ],['site_id', '=', $this->site_id] ])->findOrEmpty()->toArray();
        if ($info[ 'remain_count' ] == '-1') {
            $info[ 'limit' ] = 2;
        } else {
            $info[ 'limit' ] = 1;
        }
        if ($info[ 'min_condition_money' ] == '0.00') {
            $info[ 'threshold' ] = 2;
        } else {
            $info[ 'threshold' ] = 1;
        }
        if ($info[ 'limit_count' ] == 0) {
            $info[ 'limit_count' ] = '';
        }

        if ($info[ 'type' ] == 2) {
            $goods_coupon_model = new CouponGoods();
            $goods_coupon_list = $goods_coupon_model->where([ [ 'coupon_id', '=', $id ] ])->field('category_id')->select()->toArray();
            $info[ 'goods_category_ids' ] = array_column($goods_coupon_list, 'category_id');
        } else {
            $info[ 'goods_category_ids' ] = [];
        }
        if ($info[ 'type' ] == 3) {
            $goods_coupon_model = new CouponGoods();
            $goods_coupon_list = $goods_coupon_model->where([ [ 'coupon_id', '=', $id ] ])->field('goods_id')->select()->toArray();
            $info[ 'goods_ids' ] = array_column($goods_coupon_list, 'goods_id');
        } else {
            $info[ 'goods_ids' ] = [];
        }
        if ($info[ 'remain_count' ] != '-1') {
            $info[ 'sum_count' ] = $info[ 'remain_count' ] + $info[ 'receive_count' ];
        } else {
            $info['remain_count'] = 1000;
            $info[ 'sum_count' ] = '不限量';
        }
        //查询已使用数量
        $coupon_member_model = new CouponMember();
        $info[ 'receive_use_count' ] = $coupon_member_model->where([ [ 'coupon_id', '=', $info[ 'id' ] ], [ 'use_time', '>', 1 ] ])->count();

        //查询已过期数量
        $info[ 'receive_expire_count' ] = $coupon_member_model->where([ [ 'coupon_id', '=', $info[ 'id' ] ], [ 'use_time', '=', 0 ], [ 'expire_time', '<', time() ] ])->count();
        return $info;
    }

    /**
     * 添加优惠券
     * @param array $data
     * @return mixed
     */
    public function add(array $data)
    {
        $data['site_id'] = $this->site_id;
        if ($data[ 'threshold' ] == 2) {
            $data[ 'min_condition_money' ] = 0;
        }
        if ($data[ 'receive_type' ] == 2) {
            unset($data[ 'receive_time' ]);
            unset($data[ 'valid_time' ]);
            unset($data[ 'limit_count' ]);
            unset($data[ 'remain_count' ]);

        } else {
            if ($data[ 'receive_type_time' ] == 1) {
                if (!empty($data[ 'receive_time' ])) {
                    $data[ 'start_time' ] = strtotime($data[ 'receive_time' ][ 0 ]);;
                    $data[ 'end_time' ] = strtotime($data[ 'receive_time' ][ 1 ]);
                } else {
                    $data[ 'start_time' ] = '';
                    $data[ 'end_time' ] = '';
                }
            } else {
                $data[ 'start_time' ] = '';
                $data[ 'end_time' ] = '';
            }
            if (!empty($data[ 'valid_time' ])) {
                $data[ 'valid_start_time' ] = time();
                $data[ 'valid_end_time' ] = strtotime($data[ 'valid_time' ]);
            }
            if ($data[ 'limit' ] == 2) {
                $data[ 'remain_count' ] = '-1';
            }
        }

        if (!empty($data[ 'goods_ids' ]) || !empty($data[ 'goods_category_ids' ])) {

            $coupon_goods_model = new CouponGoods();
            if (!empty($data[ 'goods_ids' ])) {
                Db::startTrans();
                try {
                    $data[ 'create_time' ] = time();
                    $res = $this->model->create($data);
                    $coupon_goods = [];
                    foreach ($data[ 'goods_ids' ] as $value) {
                        $coupon_goods[] = [
                            'site_id' => $this->site_id,
                            'coupon_id' => $res->id,
                            'goods_id' => $value,
                        ];
                    }
                    $coupon_goods_model->saveAll($coupon_goods);
                    Db::commit();
                    return $res->id;
                } catch (\Exception $e) {
                    Db::rollback();
                    throw new CommonException($e->getMessage());
                }
            }
            if (!empty($data[ 'goods_category_ids' ])) {

                $data[ 'goods_category_ids' ] = array_count_values(call_user_func_array('array_merge', $data[ 'goods_category_ids' ]));
                foreach ($data[ 'goods_category_ids' ] as $k => $v) {
                    if ($v != 1) {
                        unset($data[ 'goods_category_ids' ][ $k ]);
                    }
                }
                $data[ 'goods_category_ids' ] = array_keys($data[ 'goods_category_ids' ]);
                Db::startTrans();
                try {
                    $data[ 'create_time' ] = time();
                    $res = $this->model->create($data);
                    $coupon_goods = [];
                    foreach ($data[ 'goods_category_ids' ] as $value) {
                        $coupon_goods[] = [
                            'site_id' => $this->site_id,
                            'coupon_id' => $res->id,
                            'category_id' => $value,
                        ];
                    }

                    $coupon_goods_model->saveAll($coupon_goods);
                    Db::commit();
                    return $res->id;
                } catch (\Exception $e) {
                    Db::rollback();
                    throw new CommonException($e->getMessage());
                }
            }
        } else {
            $data[ 'create_time' ] = time();
            $res = $this->model->create($data);
            return $res->id;
        }
    }

    /**
     * 编辑优惠券
     * @param int $id
     * @param array $data
     * @return bool
     */
    public function edit(int $id, array $data)
    {
        $coupon_member_model = new CouponMember();
        $coupon_member_info = $coupon_member_model->where([ [ 'coupon_id', '=', $id ], [ 'site_id', '=', $this->site_id ] ])->find();
        if ($coupon_member_info) {
            throw new AdminException('该优惠券已被用户领取无法修改');
        }
        if ($data[ 'threshold' ] == 2) {
            $data[ 'min_condition_money' ] = 0;
        }
        unset($data[ 'threshold' ]);
        if ($data[ 'receive_type' ] == 2) {
            unset($data[ 'receive_time' ]);
            unset($data[ 'valid_time' ]);
            unset($data[ 'limit_count' ]);
            unset($data[ 'remain_count' ]);

        } else {
            if ($data[ 'receive_type_time' ] == 1) {
                if (!empty($data[ 'receive_time' ])) {
                    $data[ 'start_time' ] = strtotime($data[ 'receive_time' ][ 0 ]);;
                    $data[ 'end_time' ] = strtotime($data[ 'receive_time' ][ 1 ]);
                } else {
                    $data[ 'start_time' ] = '';
                    $data[ 'end_time' ] = '';
                }
            } else {
                $data[ 'start_time' ] = '';
                $data[ 'end_time' ] = '';
            }

            if ($data[ 'valid_type' ] == 2) {
                if (!empty($data[ 'valid_time' ])) {
                    $data[ 'valid_start_time' ] = time();
                    $data[ 'valid_end_time' ] = strtotime($data[ 'valid_time' ]);
                }
            } else {
                $data[ 'valid_start_time' ] = '';
                $data[ 'valid_end_time' ] = '';
            }
            if ($data[ 'limit' ] == 2) {
                $data[ 'remain_count' ] = '-1';
            }

        }
        unset($data[ 'limit' ]);
        unset($data[ 'receive_time' ]);
        unset($data[ 'valid_time' ]);
        unset($data[ 'receive_type_time' ]);

        $coupon_goods_model = new CouponGoods();
        if (!empty($data[ 'goods_ids' ])) {
            Db::startTrans();
            try {

                $coupon_goods = [];
                $coupon_goods_model->where([ [ 'coupon_id', '=', $id ] ])->delete();
                foreach ($data[ 'goods_ids' ] as $value) {
                    $coupon_goods[] = [
                        'site_id' => $this->site_id,
                        'coupon_id' => $id,
                        'goods_id' => $value,
                    ];
                }
                $coupon_goods_model->saveAll($coupon_goods);
                unset($data[ 'goods_ids' ]);
                unset($data[ 'goods_category_ids' ]);
                $res = $this->model->where([ [ 'id', '=', $id ] ])->update($data);
                Db::commit();
                return $res;
            } catch (\Exception $e) {
                Db::rollback();
                throw new CommonException($e->getMessage());
            }
        } else if (!empty($data[ 'goods_category_ids' ])) {
            $data[ 'goods_category_ids' ] = array_count_values(call_user_func_array('array_merge', $data[ 'goods_category_ids' ]));
            foreach ($data[ 'goods_category_ids' ] as $k => $v) {
                if ($v != 1) {
                    unset($data[ 'goods_category_ids' ][ $k ]);
                }
            }
            $data[ 'goods_category_ids' ] = array_keys($data[ 'goods_category_ids' ]);
            Db::startTrans();
            try {

                $coupon_goods = [];
                $coupon_goods_model->where([ [ 'coupon_id', '=', $id ] ])->delete();
                foreach ($data[ 'goods_category_ids' ] as $value) {
                    $coupon_goods[] = [
                        'site_id' => $this->site_id,
                        'coupon_id' => $id,
                        'category_id' => $value,
                    ];
                }
                $coupon_goods_model->saveAll($coupon_goods);
                unset($data[ 'goods_ids' ]);
                unset($data[ 'goods_category_ids' ]);
                $res = $this->model->where([ [ 'id', '=', $id ] ])->update($data);
                Db::commit();
                return $res;
            } catch (\Exception $e) {
                Db::rollback();
                throw new CommonException($e->getMessage());
            }
        } else {
            unset($data[ 'goods_ids' ]);
            unset($data[ 'goods_category_ids' ]);
            Db::startTrans();
            try {
                $coupon_goods_model->where([ [ 'coupon_id', '=', $id ] ])->delete();
                $res = $this->model->where([ [ 'id', '=', $id ], ['site_id', '=', $this->site_id] ])->update($data);
                Db::commit();
                return $res;
            } catch (\Exception $e) {
                Db::rollback();
                throw new CommonException($e->getMessage());
            }
        }
    }

    /**
     * 删除优惠券(暂不使用)
     * @param int $id
     * @return bool
     */
    public function del(int $id)
    {
        $coupon_member_model = new CouponMember();
        $coupon_member_info = $coupon_member_model->where([ [ 'coupon_id', '=', $id ] ])->find();
        if ($coupon_member_info) {
            throw new AdminException('该优惠券已被用户领取无法删除');
        }

        $res = $this->model->where([ [ 'id', '=', $id ] ])->delete();
        return $res;
    }

    /**
     * 领取记录
     * @param int $id
     * @return void
     */
    public function getMemberCoupon($data)
    {
        $coupon_member_model = new CouponMember();
        $member_where = [];
        if (!empty($data[ 'keywords' ])) {
            $member_where = [ [ 'member.nickname|member.mobile', '=', $data[ 'keywords' ] ] ];
        }
        $memberList = $coupon_member_model->where([ [ 'coupon_id', '=', $data[ 'id' ] ] ])->withJoin([
            'member' => function($query) {
                $query->field('member_id, nickname, mobile');
            },
        ])->where($member_where)->order('id desc')->append([ 'status_name', 'receive_type_name' ]);
        $list = $this->pageQuery($memberList);
        return $list;
    }

    /**
     * 领取状态修改
     * @param $id
     * @param $status
     * @return true
     */
    public function setStatus($id, $status)
    {
        $data = array (
            'receive_status' => $status
        );
        $this->model->where([ [ 'id', '=', $id ] ])->update($data);
        return true;
    }

}
