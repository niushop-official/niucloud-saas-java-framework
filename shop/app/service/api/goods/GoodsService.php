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

namespace addon\shop\app\service\api\goods;

use addon\shop\app\model\coupon\CouponGoods;
use addon\shop\app\model\goods\Brand;
use addon\shop\app\model\goods\Goods;
use addon\shop\app\model\goods\GoodsCollect;
use addon\shop\app\model\goods\Label;
use addon\shop\app\model\goods\Service;
use addon\shop\app\model\goods\GoodsSku;
use core\base\BaseApiService;

/**
 *  商品服务层
 */
class GoodsService extends BaseApiService
{
    public function __construct()
    {
        parent::__construct();
        $this->model = new Goods();
    }

    /**
     * 获取商品列表
     * @param array $where
     * @return array
     */
    public function getPage(array $where = [])
    {
        $field = 'site_id,goods_id,goods_name,sub_title,goods_category,goods_type,goods_cover,unit,status,sale_num + goods.virtual_sale_num as sale_num';

        $sku_where = [
            [ 'goodsSku.is_default', '=', 1 ],
            [ 'goods.site_id', '=', $this->site_id ],
            [ 'status', '=', 1 ]
        ];

        if (!empty($where[ 'keyword' ])) {
            $sku_where[] = [ 'goods_name|sub_title', 'like', '%' . $where[ 'keyword' ] . '%' ];
        }

        if (!empty($where[ 'start_price' ]) && !empty($where[ 'end_price' ])) {
            $money = [ $where[ 'start_price' ], $where[ 'end_price' ] ];
            sort($money);
            $sku_where[] = [ 'goodsSku.price', 'between', $money ];
        } else if (!empty($where[ 'start_price' ])) {
            $sku_where[] = [ 'goodsSku.price', '>=', $where[ 'start_price' ] ];
        } else if (!empty($where[ 'end_price' ])) {
            $sku_where[] = [ 'goodsSku.price', '<=', $where[ 'end_price' ] ];
        }

        // 查询优惠券包括的id
        if (!empty($where[ 'coupon_id' ])) {
            $coupon_goods_model = new CouponGoods();
            $coupon_list = $coupon_goods_model->where([
                [ 'coupon_id', '=', $where[ 'coupon_id' ] ]
            ])->field('goods_id,category_id')->select()->toArray();
            if (!empty($coupon_list)) {
                $goods_ids = array_values(array_filter(array_column($coupon_list, 'goods_id')));
                $category_ids = array_values(array_filter(array_column($coupon_list, 'category_id')));
                if (!empty($goods_ids)) {
                    $sku_where[] = [ 'goods.goods_id', 'in', $goods_ids ];
                } elseif (!empty($category_ids)) {
                    $like_arr = [];
                    foreach ($category_ids as $k => $v) {
                        $like_arr[] = "%" . $v . "%";
                    }
                    $sku_where[] = [ 'goods_category', "like", $like_arr, 'or' ];
                }
            }
        }

        // 参数过滤
        if (!empty($where[ 'order' ]) && in_array($where[ 'order' ], [ 'sale_num', 'price' ])) {
            $order = $where[ 'order' ] . ' ' . $where[ 'sort' ];
        } else {
            $order = 'sort desc,create_time desc';
        }

        $search_model = $this->model
            ->withSearch([ "brand_id", "goods_category", "label_ids", 'service_ids' ], $where)
            ->field($field)
            ->withJoin([ 'goodsSku' ])
            ->where($sku_where)->order($order)->append([ 'goods_type_name', 'goods_cover_thumb_small', 'goods_cover_thumb_mid' ]);
        $list = $this->pageQuery($search_model);
        return $list;
    }

    /**
     * 获取商品详情
     * @param array $data
     * @return array
     */
    public function getDetail(array $data)
    {
        $sku_id = $data[ 'sku_id' ];
        $goods_id = $data[ 'goods_id' ];

        $goods_sku_model = new GoodsSku();

        if (empty($sku_id) && !empty($goods_id)) {
            $default_sku_info = $goods_sku_model->where([ [ 'goods_id', '=', $goods_id ], [ 'site_id', '=', $this->site_id ] ], 'sku_id')
                ->field('sku_id')->findOrEmpty()->toArray();
            if (!empty($default_sku_info)) {
                $sku_id = $default_sku_info[ 'sku_id' ];
            }
        }

        $field = 'sku_id, sku_name, sku_image, sku_no, goods_id, site_id, sku_spec_format, price, market_price, sale_price, stock, weight, volume, sale_num, is_default';

        $info = $goods_sku_model->where([ [ 'sku_id', '=', $sku_id ], [ 'site_id', '=', $this->site_id ] ])
            ->field($field)
            ->with([
                'goods' => function($query) {
                    $query->withField('goods_id, site_id, goods_name, goods_type, sub_title, goods_cover, goods_category, goods_image,goods_desc,brand_id,label_ids,service_ids, unit, stock, sale_num + virtual_sale_num as sale_num, status,delivery_type')
                        ->append([ 'goods_type_name', 'goods_cover_thumb_small', 'goods_cover_thumb_mid', 'goods_cover_thumb_big', 'delivery_type_list', 'goods_image_thumb_small', 'goods_image_thumb_mid', 'goods_image_thumb_big' ]);
                },
                // 商品规格列表
                'skuList' => function($query) {
                    $query->field('sku_id, site_id, sku_name, sku_image, sku_no, goods_id, sku_spec_format, price, market_price, sale_price, stock, weight, volume, is_default');
                },
                // 商品规格项/规格值列表
                'goodsSpec' => function($query) {
                    $query->field('spec_id, goods_id, spec_name, spec_values');
                },
            ])
            ->append([ 'sku_image_thumb_small', 'sku_image_thumb_mid', 'sku_image_thumb_big' ])
            ->findOrEmpty()->toArray();

        if (!empty($info) && !empty($info[ 'goods' ])) {
            if (!empty($info[ 'goods' ][ 'service_ids' ])) {
                // 商品服务
                $goods_service_model = new Service();
                $info[ 'service' ] = $goods_service_model->where([
                    [ 'site_id', '=', $this->site_id ],
                    [ 'service_id', 'in', $info[ 'goods' ][ 'service_ids' ] ]
                ])->field('service_id, service_name, image, desc')
                    ->select()->toArray();
            }
            if (!empty($info[ 'goods' ][ 'label_ids' ])) {
                // 商品标签
                $goods_label_model = new Label();
                $info[ 'label_info' ] = $goods_label_model->where([
                    [ 'site_id', '=', $this->site_id ],
                    [ 'label_id', 'in', $info[ 'goods' ][ 'label_ids' ] ]
                ])->field('label_id, label_name, memo')
                    ->select()->toArray();
            }
            if (!empty($info[ 'goods' ][ 'brand_id' ])) {
                // 商品品牌
                $goods_brand_model = new Brand();
                $info[ 'brand_info' ] = $goods_brand_model->where([
                    [ 'site_id', '=', $this->site_id ],
                    [ 'brand_id', '=', $info[ 'goods' ][ 'brand_id' ] ]
                ])->field('brand_id, brand_name, logo, desc')
                    ->findOrEmpty()->toArray();
            }

            if (!empty($this->member_id)) {
                $goods_collect_model = new GoodsCollect();
                $collect_info = $goods_collect_model->where([ [ 'site_id', '=', $this->site_id ],[ 'member_id', '=', $this->member_id ], [ 'goods_id', '=', $goods_id ] ])->findOrEmpty()->toArray();
                if (!empty($collect_info)) {
                    $info[ 'goods' ][ 'is_collect' ] = 1;
                } else {
                    $info[ 'goods' ][ 'is_collect' ] = 0;
                }
            } else {
                $info[ 'goods' ][ 'is_collect' ] = 0;
            }
        }
        return $info;
    }

    /**
     * 获取商品规格信息，切换规格
     * @param int $sku_id
     * @return array
     */
    public function getSku(int $sku_id)
    {

        $field = 'sku_id, sku_name, sku_image, sku_no, goods_id, sku_spec_format, price, market_price, sale_price, stock, weight, volume, sale_num, is_default';

        $goods_sku_model = new GoodsSku();

        $info = $goods_sku_model->where([ [ 'site_id', '=', $this->site_id ],[ 'sku_id', '=', $sku_id ] ])
            ->field($field)
            ->with([
                // 商品主表
                'goods' => function($query) {
                    $query->withField('goods_id, goods_name, goods_type, sub_title, goods_cover, unit, stock, sale_num + virtual_sale_num as sale_num, status')
                        ->append([ 'goods_type_name', 'goods_cover_thumb_small', 'goods_cover_thumb_mid', 'goods_cover_thumb_big' ]);
                },
                // 商品规格列表
                'skuList' => function($query) {
                    $query->field('sku_id, sku_name, sku_image, sku_no, goods_id, sku_spec_format, price, market_price, sale_price, stock, weight, volume, is_default');
                },
                // 商品规格项/规格值列表
                'goodsSpec' => function($query) {
                    $query->field('spec_id, goods_id, spec_name, spec_values');
                },
            ])
            ->append([ 'sku_image_thumb_small', 'sku_image_thumb_mid', 'sku_image_thumb_big' ])
            ->findOrEmpty()->toArray();

        return $info;
    }

    /**
     * 获取商品列表供组件调用
     * @param array $where
     * @return array
     */
    public function getGoodsComponents(array $where = [])
    {
        $field = 'goods_id,site_id,goods_name,sub_title,goods_category,goods_type,goods_cover,unit,status,sale_num + goods.virtual_sale_num as sale_num';

        $sku_where = [
            [ 'goodsSku.is_default', '=', 1 ],
            [ 'goodsSku.site_id', '=', $this->site_id ],
            [ 'status', '=', 1 ]
        ];

        if (!empty($where[ 'goods_ids' ])) {
            $sku_where[] = [ 'goods.goods_id', 'in', $where[ 'goods_ids' ] ];
        }

        // 参数过滤
        if (!empty($where[ 'order' ]) && in_array($where[ 'order' ], [ 'sale_num', 'price' ])) {
            $order = $where[ 'order' ] . ' desc';
        } else {
            $order = 'sort desc,create_time desc';
        }

        $list = $this->model
            ->withSearch([ "goods_category", "label_ids", 'service_ids' ], $where)
            ->field($field)
            ->withJoin([ 'goodsSku' ])
            ->where($sku_where)->order($order)->append([ 'goods_type_name', 'goods_cover_thumb_small', 'goods_cover_thumb_mid' ])
            ->limit($where[ 'num' ])
            ->select()->toArray();
        return $list;
    }

}
