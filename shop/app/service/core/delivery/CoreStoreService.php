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

namespace addon\shop\app\service\core\delivery;

use addon\shop\app\dict\goods\GoodsDict;
use addon\shop\app\dict\order\OrderDeliveryDict;
use addon\shop\app\model\delivery\Store;
use core\base\BaseCoreService;
use Location\Coordinate;
use Location\Distance\Vincenty;

/**
 * 门店自提服务层
 * Class CoreExpressService
 * @package addon\shop\app\service\admin\delivery
 */
class CoreStoreService extends BaseCoreService
{
    public function __construct()
    {
        parent::__construct();
        $this->model = new Store();
    }

    /**
     * 配送费用计算
     * @param $order
     * @return void
     */
    public static function calculate(&$order)
    {
        foreach ($order->goods_data as $k => &$v) {
            $goods_type = $v[ 'goods' ][ 'goods_type' ];
            if ($goods_type == GoodsDict::REAL) {
                if (!in_array(OrderDeliveryDict::STORE, $v[ 'goods' ][ 'delivery_type' ])) {
                    $v[ 'not_support_delivery' ] = 1;
                    $order->error[] = get_lang('NOT_SUPPORT_DELIVERY_TYPE');
                }
            }
        }
    }

    /**
     * 查询自提点
     * @param int $id
     * @return array
     */
    public function getInfoById(int $store_id)
    {
        $condition = array (
            [ 'store_id', '=', $store_id ]
        );
        return $this->model->where($condition)->findOrEmpty()->toArray();
    }

    /**
     * 查询自提点
     * @param $latlng
     * @return array
     * @throws \think\db\exception\DataNotFoundException
     * @throws \think\db\exception\DbException
     * @throws \think\db\exception\ModelNotFoundException
     */
    public function getStoreList($site_id, $latlng = [])
    {
        $list = $this->model->where([ ['site_id', '=', $site_id ] ])->field('store_id,store_name,store_logo,store_mobile,full_address,longitude,latitude,trade_time')->select()->toArray();
        if (!empty($list) && !empty($latlng)) {
            $location = new Coordinate($latlng[ 'lat' ], $latlng[ 'lng' ]);
            $list = array_map(function($item) use ($location) {
                $item[ 'distance' ] = ( new Vincenty() )->getDistance($location, new Coordinate($item[ 'latitude' ], $item[ 'longitude' ]));
                return $item;
            }, $list);
            array_multisort(array_column($list, 'distance'), SORT_ASC, $list);
        }
        return $list;
    }
}
