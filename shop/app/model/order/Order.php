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
use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\model\delivery\Store;
use app\dict\common\ChannelDict;
use app\model\member\Member;
use app\model\pay\Pay;
use core\base\BaseModel;
use think\db\Query;
use think\model\relation\HasMany;
use think\model\relation\HasOne;

/**
 * 订单模型
 */
class Order extends BaseModel
{

    /**
     * 数据表主键
     * @var string
     */
    protected $pk = 'order_id';

    /**
     * 模型名称
     * @var string
     */
    protected $name = 'shop_order';

    //类型
    protected $type = [
        'pay_time' => 'timestamp',
        'close_time' => 'timestamp',

        'delivery_time' => 'timestamp',
        'take_time' => 'timestamp',
        'finish_time' => 'timestamp',
    ];

    // 设置json类型字段
    protected $json = ['has_goods_types'];

    // 设置JSON数据返回数组
    protected $jsonAssoc = true;

    /**
     * 订单项
     * @return HasMany
     */
    public function orderGoods()
    {
        return $this->hasMany(OrderGoods::class, 'order_id', 'order_id');
    }

    /**
     * 订单会员
     * @return HasOne
     */
    public function member()
    {
        return $this->hasOne(Member::class, 'member_id', 'member_id');
    }

    /**
     * 支付
     * @return HasOne
     */
    public function pay()
    {
        return $this->hasOne(Pay::class, 'out_trade_no', 'out_trade_no');
    }

    /**
     * 日志
     */
    public function orderLog()
    {
        return $this->hasMany(OrderLog::class, 'order_id', 'order_id');
    }

    /**
     * 自提点
     * @return HasOne
     */
    public function store()
    {
        return $this->hasOne(Store::class, 'store_id', 'take_store_id');
    }

    /**
     * 包裹
     * @return HasOne
     */
    public function orderDelivery()
    {
        return $this->hasMany(OrderDelivery::class, 'order_id', 'order_id');
    }

    /**
     * 来源渠道
     * @param $value
     * @param $data
     * @return mixed|string|void
     */
    public function getOrderFromNameAttr($value, $data)
    {
        if (empty($data['order_from']))
            return '';
        return ChannelDict::getType()[$data['order_from']] ?? '';
    }

    /**
     * 订单类型
     * @param $value
     * @param $data
     * @return mixed|string
     */
    public function getOrderTypeNameAttr($value, $data)
    {
        if (empty($data['order_type']))
            return '';
//        return OrderDict::getType()[$data['order_type']] ?? '';
    }

    /**
     * 订单状态
     * @param $value
     * @param $data
     * @return mixed|string
     */
    public function getStatusNameAttr($value, $data)
    {
        if (empty($data['status']))
            return '';
        return OrderDict::getStatus()[$data['status']] ?? '';
    }

    /**
     * 配送方式
     * @param $value
     * @param $data
     * @return mixed|string
     */
    public function getDeliveryTypeNameAttr($value, $data)
    {
        if (empty($data['delivery_type']))
            return '';
        return OrderDeliveryDict::getType()[$data['delivery_type']] ?? '';
    }

    /**
     * 搜索器:订单编号
     * @param $value
     * @param $data
     */
    public function searchOrderNoAttr($query, $value, $data)
    {
        if ($value) {
            $query->where("order_no", $value);
        }
    }

    /**
     * 搜索器:订单状态
     * @param $value
     * @param $data
     */
    public function searchStatusAttr($query, $value, $data)
    {
        if ($value) {
            $query->where("status", $value);
        }
    }

    /**
     * 连表搜索器
     * @param $query
     * @param $value
     * @param $data
     */
    public function searchJoinStatusAttr($query, $value, $data)
    {
        if ($data['status']) {
            $query->where("order.status", $data['status']);
        }
    }
    /**
     * 支付时间搜索器
     * @param Query $query
     * @param $value
     * @param $data
     */
    public function searchPayTimeAttr(Query $query, $value, $data)
    {
        $start_time = empty($value[0]) ? 0 : strtotime($value[0]);
        $end_time = empty($value[1]) ? 0 : strtotime($value[1]);
        if ($start_time > 0 && $end_time > 0) {
            $query->whereBetweenTime('pay_time', $start_time, $end_time);
        } else if ($start_time > 0 && $end_time == 0) {
            $query->where([['pay_time', '>=', $start_time]]);
        } else if ($start_time == 0 && $end_time > 0) {
            $query->where([['pay_time', '<=', $end_time]]);
        }
    }

    /**
     * 支付时间搜索器
     * @param Query $query
     * @param $value
     * @param $data
     */
    public function searchJoinPayTimeAttr(Query $query, $value, $data)
    {
        $start_time = empty($data['pay_time'][0]) ? 0 : strtotime($data['pay_time'][0]);
        $end_time = empty($data['pay_time'][1]) ? 0 : strtotime($data['pay_time'][1]);
        if ($start_time > 0 && $end_time > 0) {
            $query->whereBetweenTime('order.pay_time', $start_time, $end_time);
        } else if ($start_time > 0 && $end_time == 0) {
            $query->where([['order.pay_time', '>=', $start_time]]);
        } else if ($start_time == 0 && $end_time > 0) {
            $query->where([['order.pay_time', '<=', $end_time]]);
        }
    }


    /**
     * 创建时间搜索器
     * @param Query $query
     * @param $value
     * @param $data
     */
    public function searchCreateTimeAttr(Query $query, $value, $data)
    {
        $start_time = empty($value[0]) ? 0 : strtotime($value[0]);
        $end_time = empty($value[1]) ? 0 : strtotime($value[1]);
        if ($start_time > 0 && $end_time > 0) {
            $query->whereBetweenTime('create_time', $start_time, $end_time);
        } else if ($start_time > 0 && $end_time == 0) {
            $query->where([['create_time', '>=', $start_time]]);
        } else if ($start_time == 0 && $end_time > 0) {
            $query->where([['create_time', '<=', $end_time]]);
        }
    }

    /**
     * 搜索器:订单状态
     * @param $value
     * @param $data
     */
    public function searchSearchTypeAttr($query, $value, $data)
    {
        if ($value && $data['search_name']) {
            if($value == 'order_no') $query->where("order_no", $data['search_name']);
            if($value == 'order_trade_no') $query->where("order_trade_no", $data['search_name']);
        }
    }

    /**
     * 搜索器:订单状态
     * @param $value
     * @param $data
     */
    public function searchOrderFromAttr($query, $value, $data)
    {
        if ($value) {
            $query->where("order_from", $value);
        }
    }
}
