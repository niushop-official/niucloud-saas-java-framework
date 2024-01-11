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
namespace core\sms;

use addon\shop\app\service\core\delivery\delivery_search\BaseDeliverySearch;
use AlibabaCloud\Client\AlibabaCloud;
use core\exception\NoticeException;
use Exception;


class Kd100DeliverySearch extends BaseDeliverySearch
{

    protected $app_key = '';
    protected $secret_key = '';
    protected $sign = '';

    /**
     * @param array $config
     * @return void
     */
    protected function initialize(array $config = [])
    {
        parent::initialize($config);
        $this->app_key = $config['app_key'] ?? '';
        $this->secret_key = $config['secret_key'] ?? '';
        $this->sign = $config['sign'] ?? '';
    }


    /**
     * 发送短信
     * @param string $mobile
     * @param string $template_id
     * @param array $data
     * @return array
     */
    public function search(string $mobile, string $template_id, array $data = [])
    {
        //查询数据
    }
}