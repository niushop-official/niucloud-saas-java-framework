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

use addon\shop\app\dict\delivery\ShippingTemplateDict;
use addon\shop\app\dict\goods\GoodsDict;
use addon\shop\app\dict\order\OrderDeliveryDict;
use addon\shop\app\model\delivery\ShippingTemplateItem;
use core\base\BaseCoreService;

/**
 * 快递配送服务层
 * Class CoreExpressService
 * @package addon\shop\app\service\admin\delivery
 */
class CoreExpressService extends BaseCoreService
{
    /**
     * 配送费用计算
     * @param $order
     * @return void
     */
    public static function calculate(&$order) {
        $delivery_money = 0;
        $address =  $order->delivery['take_address'] ?? [];

        if (empty($address)) {
            $order->error[] = get_lang('NOT_SELECT_ADDRESS');
            return;
        }

        foreach ($order->goods_data as $k => &$v) {
            $goods_type = $v['goods']['goods_type'];
            if ($goods_type == GoodsDict::REAL && $v['goods']['is_free_shipping'] == 0) {
                if (in_array(OrderDeliveryDict::EXPRESS, $v['goods']['delivery_type'])) {
                    if ($v['goods']['fee_type'] == 'template') {
                        (new self())->feeCalculate($order, $v);
                    } else {
                        $v['delivery_money'] = $v['goods']['delivery_money'] * $v['num'];
                    }
                    $delivery_money += $v['delivery_money'] ?? 0;
                } else {
                    $v['not_support_delivery'] = 1;
                    $order->error[] = get_lang('NOT_SUPPORT_DELIVERY_TYPE');
                }
            }
        }
        $order->basic['delivery_money'] = $delivery_money;
    }

    private function feeCalculate(&$order, &$goods) {
        $address =  $order->delivery['take_address'] ?? [];

        $field = 'snum,sprice,xnum,xprice,fee_type,fee_area_ids,no_delivery,no_delivery_area_ids,is_free_shipping,free_shipping_area_ids,free_shipping_price,free_shipping_num';
        $template = (new ShippingTemplateItem())->where([ ['template_id', '=', $goods['goods']['delivery_template_id'] ], ['city_id', '=', $address['city_id'] ] ])->field($field)->findOrEmpty();
        if ($template->isEmpty()) $template = (new ShippingTemplateItem())->where([ ['template_id', '=', $goods['goods']['delivery_template_id'] ], ['city_id', '=', 0 ] ])->field($field)->findOrEmpty();

        switch ($template['fee_type']) {
            case ShippingTemplateDict::NUM:
                $total = $goods['num'];
                break;
            case ShippingTemplateDict::VOLUME:
                $total = $goods['volume'] * $goods['num'];
                break;
            case ShippingTemplateDict::WEIGHT:
                $total = $goods['weight'] * $goods['num'];
                break;
        }

        // 判断收货地址是否不配送
        if ($template['no_delivery'] && !empty($template['no_delivery_area_ids'])) {
            $goods['not_support_delivery'] = 1;
            $order->error[] = get_lang('NOT_SUPPORT_DELIVERY_ADDRESS');
            return true;
        }

        // 判断收货地址是否包邮
        if ($template['is_free_shipping'] && !empty($template['free_shipping_area_ids']) && ( $total > $template['free_shipping_num'] || $goods['good_money'] > $template['free_shipping_num'])) {
            $goods['is_free_shipping'] = 1;
            return true;
        }

        // 计算费用
        $goods['delivery_money'] = $template['sprice'];
        if ($total > $template['snum']) {
            $goods['delivery_money'] += round(ceil(($total - $template['snum']) / $template['xnum']) * $template['xprice'], 2);
        }
    }

}
