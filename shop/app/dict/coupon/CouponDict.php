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

namespace addon\shop\app\dict\coupon;

class CouponDict
{

    const ALL = 1;
    const CATEGORY = 2;
    const GOODS = 3;

    const USER = 1;
    const GRANT = 2;
    /**
     * 优惠券类型
     * @param $status
     * @return array|mixed|string
     */
    public static function getType($type = ''){
        $list = [
            self::ALL => get_lang('dict_shop_coupon.all'),
            self::CATEGORY => get_lang('dict_shop_coupon.category'),
            self::GOODS => get_lang('dict_shop_coupon.goods'),
        ];
        if ($type == '') return $list;
        return $list[$type] ?? '';
    }

    /**
     * 领取优惠券类型
     * @param $status
     * @return array|mixed|string
     */
    public static function getReceiveType($type = ''){
        $list = [
            self::USER => get_lang('dict_shop_coupon.user'),
            self::GRANT => get_lang('dict_shop_coupon.grant'),
        ];
        if ($type == '') return $list;
        return $list[$type] ?? '';
    }


    //未开始
    const WAIT_START = 1;
    //进行中
    const NORMAL = 2;
    //已过期
    const EXPIRE = 3;
    //已失效
    const INVALID = 4;
    /**
     * 优惠券活动状态
     * @param $status
     * @return array|mixed|string
     */
    public static function getStatus($status = ''){
        $list = [
            self::WAIT_START => get_lang('dict_shop_coupon.wait_start'),
            self::NORMAL => get_lang('dict_shop_coupon.normal'),
            self::EXPIRE => get_lang('dict_shop_coupon.expire'),
            self::INVALID => get_lang('dict_shop_coupon.invalid'),
        ];
        if ($status == '') return $list;
        return $list[$status] ?? '';
    }
}
