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
namespace addon\shop\app\listener;

/**
 * 站点创建之后
 */
class AddSiteAfterListener
{
    public function handle($params = [])
    {
        if (in_array('shop', $params[ 'main_app' ]) && count($params[ 'main_app' ]) == 1) {
            $site_id = $params[ 'site_id' ];
            request()->siteId($site_id);
            return true;
        }
    }
}
