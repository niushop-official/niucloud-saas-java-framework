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

namespace addon\shop\app\model\delivery;

use core\base\BaseModel;

/**
 * 物流公司模型
 * Class Company
 * @package addon\shop\app\model\delivery
 */
class Company extends BaseModel
{


    /**
     * 数据表主键
     * @var string
     */
    protected $pk = 'company_id';

    /**
     * 模型名称
     * @var string
     */
    protected $name = 'shop_delivery_company';

    /**
     * 搜索器:物流公司
     * @param $value
     * @param $data
     */
    public function searchCompanyIdAttr($query, $value, $data)
    {
        if ($value) {
            $query->where("company_id", $value);
        }
    }

    /**
     * 搜索器:物流公司物流公司名称
     * @param $value
     * @param $data
     */
    public function searchCompanyNameAttr($query, $value, $data)
    {
        if ($value) {
            $query->where("company_name", 'like', '%' . $value . '%');
        }
    }
}
