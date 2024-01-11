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

namespace addon\shop\app\service\admin\order;


use addon\shop\app\dict\order\OrderLogDict;
use addon\shop\app\model\order\Order;
use addon\shop\app\service\core\order\CoreOrderFinishService;
use core\base\BaseAdminService;

/**
 *  订单配送服务层
 */
class OrderFinishService extends BaseAdminService
{

    public function __construct()
    {
        parent::__construct();
        $this->model = new Order();
    }

    /**
     * 完成
     * @param $data
     * @return true
     */
    public function finish(int $order_id)
    {
        $data[ 'main_type' ] = OrderLogDict::STORE;
        $data[ 'main_id' ] = $this->uid;
        $data[ 'order_id' ] = $order_id;
        $data[ 'site_id' ] = $this->site_id;
        ( new CoreOrderFinishService() )->finish($data);
        return true;
    }

}
