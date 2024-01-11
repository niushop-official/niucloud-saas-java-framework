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

namespace addon\shop\app\service\api\cart;

use addon\shop\app\model\cart\Cart;
use addon\shop\app\service\core\cart\CoreCartService;
use core\base\BaseApiService;

/**
 * 购物车服务层
 * Class CartService
 */
class CartService extends BaseApiService
{

    public function __construct()
    {
        parent::__construct();
        $this->model = new Cart();
    }

    /**
     * 添加购物车
     * @param $data
     * @return mixed
     */
    public function add($data)
    {
        $data[ 'member_id' ] = $this->member_id;
        $data[ 'site_id' ] = $this->site_id;
        $info = $this->model->where([
            [ 'member_id', '=', $data[ 'member_id' ] ],
            [ 'goods_id', '=', $data[ 'goods_id' ] ],
            [ 'sku_id', '=', $data[ 'sku_id' ] ],
            [ 'site_id', '=', $this->site_id ],
        ])->field('id,num')->findOrEmpty()->toArray();

        if (!empty($info)) {
            // 存在，更新数量
            $update = [
                'num' => $info[ 'num' ] + $data[ 'num' ]
            ];
            $this->model->where([ [ 'id', '=', $info[ 'id' ] ] ])->update($update);
            return $info[ 'id' ];
        } else {
            // 添加
            $res = $this->model->create($data);
            return $res->id;
        }
    }

    /**
     * 编辑购物车数量
     * @param $data
     * @return bool
     */
    public function edit($data)
    {
        $data[ 'member_id' ] = $this->member_id;
        $data[ 'site_id' ] = $this->site_id;
        $info = $this->model->where([
            [ 'id', '=', $data[ 'id' ] ],
            [ 'member_id', '=', $data[ 'member_id' ] ],
            [ 'site_id', '=', $data[ 'site_id' ] ],
        ])->field('sku_id')->findOrEmpty()->toArray();

        if (empty($info)) return false;

        $update = [
            'num' => $data[ 'num' ]
        ];

        if (!empty($data[ 'sku_id' ]) && $info[ 'sku_id' ] != $data[ 'sku_id' ]) {
            $update[ 'sku_id' ] = $data[ 'sku_id' ];

            $cart_info = $this->model->where([
                [ 'sku_id', '=', $data[ 'sku_id' ] ],
                [ 'member_id', '=', $this->member_id ],
            ])->field('id')->findOrEmpty()->toArray();

            if (!empty($cart_info)) {
                $this->model->where([ [ 'id', '=', $cart_info[ 'id' ] ] ])->delete();
            }
        }

        $this->model->where([ [ 'id', '=', $data[ 'id' ] ] ])->update($update);
        return true;
    }

    /**
     * 购物车删除，支持批量
     * @param $data
     * @return bool
     */
    public function del($data)
    {
        $this->model->where([ [ 'member_id', '=', $this->member_id ], ['site_id', '=', $this->site_id], [ 'id', 'in', $data[ 'ids' ] ] ])->delete();
        return true;
    }

    /**
     * 清空购物车
     * @return bool
     */
    public function clear()
    {
        $this->model->where([ [ 'member_id', '=', $this->member_id ], ['site_id', '=', $this->site_id] ])->delete();
        return true;
    }

    /**
     * 获取购物车列表
     * @param $data
     * @return array
     */
    public function getList($data)
    {
        $data[ 'member_id' ] = $this->member_id;
        $field = 'id, member_id, goods_id, site_id, sku_id, num, market_type, market_type_id, status, invalid_remark';
        $order = "create_time desc";
        return $this->model->where([ ['site_id', '=', $this->site_id] ])->withSearch([ "member_id" ], $data)->field($field)
            ->with([
                'goodsSku' => function($query) {
                    $query->withField('sku_id, site_id, sku_image, price, sale_price, stock');
                },
                'goods' => function($query) {
                    $query->withField('goods_id, site_id, status,delete_time');
                },
            ])
            ->select()->order($order)->toArray();
    }

    /**
     * 获取购物车商品列表
     * @param $data
     * @return array
     */
    public function getGoodsList($data)
    {
        $data[ 'member_id' ] = $this->member_id;
        $field = 'id, member_id, goods_id, sku_id, num, market_type, market_type_id, status, invalid_remark';
        $order = "create_time desc";
        return $this->model->where([ ['site_id', '=', $this->site_id] ])->withSearch([ "member_id" ], $data)->field($field)
            ->with([
                'goods' => function($query) {
                    $query->withField('goods_id, site_id, goods_name, goods_type, sub_title, goods_cover, unit, stock, sale_num + virtual_sale_num as sale_num, status,delete_time');
                },
                'goodsSku' => function($query) {
                    $query->withField('sku_id, site_id, sku_name, sku_image, sku_no, goods_id, sku_spec_format, price, market_price, sale_price, stock, weight, volume, is_default');
                },
                // 商品规格项/规格值列表
                'goodsSpec' => function($query) {
                    $query->field('spec_id, goods_id, spec_name, spec_values');
                },
            ])
            ->select()->order($order)->toArray();

        return ( new CoreCartService() )->getGoodsList($data);
    }

    /**
     * 获取购物车数量
     * @param $data
     * @return float
     */
    public function getSum($data)
    {
        $condition = [
            [ 'member_id', '=', $this->member_id ],
            [ 'site_id', '=', $this->site_id ]
        ];
        if (!empty($data[ 'goods_id' ])) {
            $condition[] = [ 'goods_id', '=', $data[ 'goods_id' ] ];
        }
        return $this->model->where($condition)->sum('num');
    }

}
