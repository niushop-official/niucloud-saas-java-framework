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
namespace addon\shop\app\job\order;

use addon\shop\app\dict\order\OrderDict;
use addon\shop\app\dict\order\OrderLogDict;
use addon\shop\app\model\order\Order;
use addon\shop\app\service\core\order\CoreOrderCloseService;
use addon\shop\app\service\core\order\CoreOrderFinishService;
use core\base\BaseJob;

/**
 * 订单自动收货完成
 */
class OrderFinish extends BaseJob
{
    /**
     * 消费
     * @return true
     */
    public function doJob()
    {
        try {
            $data = [];
            $data['main_type'] = OrderLogDict::SYSTEM;
            $data['main_id'] = 0;
            $list = (new Order())->where([
                ['status', '=', OrderDict::WAIT_TAKE],
                ['timeout', '<=', time()]
            ])->select();
            if(!$list->isEmpty()){
                foreach($list as $v){
                    $data['order_id'] = $v['order_id'];
                    (new CoreOrderFinishService())->finish($data);
                }
            }

            return true;
        } catch (\Exception $e) {
            return false;
        }
    }

}
