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

namespace addon\shop\app\dict\delivery;


/**
 *订单配送相关枚举类
 */
class DeliveryDict
{
    //订单状态
    //快递
    const EXPRESS = 'express';
    //同城
    const LOCAL_DELIVERY = 'local_delivery';
    //自提
    const STORE = 'store';

    /**
     * 获取配送方式(用于订单)
     * @param string $type
     * @return array|array[]|string
     */
    public static function getType(string $type = '')
    {
        $data = [
            self::EXPRESS => get_lang('dict_shop_delivery_type.express'),
            self::LOCAL_DELIVERY => get_lang('dict_shop_delivery_type.local_delivery'),
            self::STORE => get_lang('dict_shop_delivery_type.store'),
        ];

        if ($type == '') {
            return $data;
        }
        return $data[$type] ?? '';
    }
}
