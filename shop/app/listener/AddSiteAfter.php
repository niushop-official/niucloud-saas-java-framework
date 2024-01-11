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

use app\service\admin\diy\DiyConfigService;
use app\service\admin\diy\DiyService;

/**
 * 站点创建之后
 */
class AddSiteAfter
{
    public function handle($params = [])
    {
        if (in_array('shop', $params['main_app']) && count($params['main_app']) == 1) {
            $site_id = $params['site_id'];
            request()->siteId($site_id);

            $diy_service = new DiyService();

            // 设置 商城 首页 模板
            $index_flag = 'DIY_SHOP_INDEX';
            $index_template_name = 'shop_index';
            $index_template = $diy_service->getPageData($index_flag, $index_template_name);

            $diy_service->add([
                "title" => $index_template[ 'title' ],
                "name" => "DIY_INDEX",
                "type" => "DIY_INDEX",
                "template" => $index_template_name,
                "mode" => 'diy',
                "value" => json_encode($index_template['data']),
                "is_default" => 1,
                "is_change" => 0
            ]);
            // 设置 商城 个人中心 模板
            $member_index_flag = 'DIY_SHOP_MEMBER_INDEX';
            $member_index_template_name = 'shop_member_index';
            $member_index_template = $diy_service->getPageData($member_index_flag, $member_index_template_name);

            $diy_service->add([
                "title" => $member_index_template[ 'title' ],
                "name" => "DIY_MEMBER_INDEX",
                "type" => "DIY_MEMBER_INDEX",
                "template" => $member_index_template_name,
                "mode" => 'diy',
                "value" => json_encode($member_index_template['data']),
                "is_default" => 1,
                "is_change" => 0
            ]);

            // 设置 商城 底部导航
            $diy_config_service = new DiyConfigService();
            $menu = [
                'backgroundColor' => '#ffffff',
                'textColor' => '#333333',
                'textHoverColor' => '#FF4500',
                'type' => 1,
                'list' => [
                    [
                        "text" => "首页",
                        "link" => [
                            "parent" => "",
                            "name" => "INDEX",
                            "title" => get_lang('dict_diy.system_link_index'),
                            "url" => "/app/pages/index/index"
                        ],
                        "iconPath" => "addon/shop/diy/tabbar/system-home.png",
                        "iconSelectPath" => "addon/shop/diy/tabbar/system-home-selected.png"
                    ],
                    [
                        "text" => "商品分类",
                        "link" => [
                            "parent" => "SHOP_LINK",
                            "name" => "SHOP_GOODS_CATEGORY",
                            "title" => "商品分类",
                            "url" => "/addon/shop/pages/goods/category"
                        ],
                        "iconPath" => "addon/shop/diy/tabbar/system-category.png",
                        "iconSelectPath" => "addon/shop/diy/tabbar/system-category-selected.png"
                    ],
                    [
                        "text" => "购物车",
                        "link" => [
                            "parent" => "SHOP_LINK",
                            "name" => "SHOP_GOODS_CART",
                            "title" => "购物车",
                            "url" => "/addon/shop/pages/goods/cart"
                        ],
                        "iconPath" => "addon/shop/diy/tabbar/system-cart.png",
                        "iconSelectPath" => "addon/shop/diy/tabbar/system-cart-selected.png"
                    ],
                    [
                        "text" => "我的",
                        "link" => [
                            "parent" => "",
                            "name" => "MEMBER_CENTER",
                            "title" => get_lang('dict_diy.member_index'),
                            "url" => "/app/pages/member/index"
                        ],
                        "iconPath" => "addon/shop/diy/tabbar/system-my.png",
                        "iconSelectPath" => "addon/shop/diy/tabbar/system-my-selected.png"
                    ]
                ]
            ];
            $diy_config_service->setBottomConfig($menu);
            return true;
        }
    }
}
