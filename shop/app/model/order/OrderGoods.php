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

namespace addon\shop\app\model\order;

use addon\shop\app\dict\order\OrderDeliveryDict;
use addon\shop\app\dict\order\OrderGoodsDict;
use app\dict\sys\FileDict;
use core\base\BaseModel;
use Exception;
use think\model\relation\HasOne;

/**
 * 订单模型
 */
class OrderGoods extends BaseModel
{

    /**
     * 数据表主键
     * @var string
     */
    protected $pk = 'order_goods_id';

    /**
     * 模型名称
     * @var string
     */
    protected $name = 'shop_order_goods';

    //类型
    protected $type = [

    ];

    /**
     * 订单主表
     * @return BaseModel|HasOne
     */
    public function orderMain()
    {
        return $this->hasOne(Order::class, 'order_id', 'order_id');
    }

    /**
     * 缩略图生成-大图
     * @param $value
     * @param $data
     * @return array|mixed
     * @throws Exception
     */
    public function getGoodsImageThumbBigAttr($value, $data)
    {
        if (!empty($data['goods_image'])) {
            return get_thumb_images($data['site_id'], $data['goods_image'], FileDict::BIG);
        }
        return '';
    }

    /**
     * 缩略图生成-中图
     * @param $value
     * @param $data
     * @return array|mixed
     * @throws Exception
     */
    public function getGoodsImageThumbMidAttr($value, $data)
    {
        if (!empty($data['goods_image'])) {
            return get_thumb_images($data['site_id'], $data['goods_image'], FileDict::MID);
        }
        return '';
    }

    /**
     * 缩略图生成-小图
     * @param $value
     * @param $data
     * @return array|mixed
     * @throws Exception
     */
    public function getGoodsImageThumbSmallAttr($value, $data)
    {
        if (!empty($data['goods_image'])) {
            return get_thumb_images($data['site_id'], $data['goods_image'], FileDict::SMALL);
        }
        return '';
    }

    /**
     * 转化发货状态
     */
    public function getDeliveryStatusNameAttr($value, $data)
    {
        if (!empty($data['delivery_status'])) {
            return OrderDeliveryDict::getStatus($data['delivery_status']);
        }
        return '';
    }

    /**
     * 转化订单项状态
     */
    public function getStatusNameAttr($value, $data)
    {
        if (!empty($data['status'])) {
            return OrderGoodsDict::getRefundStatus($data['status']);
        }
        return '';
    }
}
