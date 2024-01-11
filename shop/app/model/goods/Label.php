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

namespace addon\shop\app\model\goods;

use core\base\BaseModel;

/**
 * 商品标签模型
 * Class Label
 * @package addon\shop\app\model\goods
 */
class Label extends BaseModel
{
    /**
     * 数据表主键
     * @var string
     */
    protected $pk = 'label_id';

    /**
     * 模型名称
     * @var string
     */
    protected $name = 'shop_goods_label';

    /**
     * 搜索器:商品标签标签名称
     * @param $value
     * @param $data
     */
    public function searchLabelNameAttr($query, $value, $data)
    {
        if ($value) {
            $query->where("label_name", "like", "%" . $value . "%");
        }
    }

}
