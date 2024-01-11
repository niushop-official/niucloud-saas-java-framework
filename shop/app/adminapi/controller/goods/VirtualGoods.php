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

namespace addon\shop\app\adminapi\controller\goods;

use addon\shop\app\service\admin\goods\VirtualGoodsService;
use core\base\BaseAdminController;


/**
 * 虚拟商品控制器
 * Class VirtualGoods
 * @package addon\shop\app\adminapi\controller\goods
 */
class VirtualGoods extends BaseAdminController
{
    /**
     * 获取商品添加/编辑数据
     * @return \think\Response
     */
    public function init()
    {
        $data = $this->request->params([
            [ "goods_id", 0 ],
        ]);
        return success(( new VirtualGoodsService() )->getInit($data));
    }

    /**
     * 添加商品
     * @return \think\Response
     */
    public function add()
    {
        $data = $this->request->params([
            [ "goods_name", "" ],
            [ "sub_title", "" ],
            [ "goods_type", "" ],
            [ "goods_image", "" ],
            [ "goods_category", '' ],
            [ "brand_id", 0 ],
            [ "label_ids", "" ],
            [ 'service_ids', '' ],
            [ 'supplier_id', 0 ],
            [ "status", 0 ],
            [ "sort", 0 ],

            // 规格类型，single：单规格，multi：多规格
            [ 'spec_type', '' ],

            // 单规格数据
            [ "price", 0 ],
            [ "market_price", 0 ],
            [ "cost_price", 0 ],
            [ "price", 0 ],
            [ "stock", 0 ],
            [ "sku_no", '' ],
            [ "unit", "件" ],
            [ "virtual_sale_num", 0 ],

            // 多规格数据
            [ 'goods_spec_format', '' ],
            [ 'goods_sku_data', '' ],

            // 商品详情
            [ "goods_desc", "" ],
        ]);

        $this->validate($data, 'addon\shop\app\validate\goods\Goods.add');
        $id = ( new VirtualGoodsService() )->add($data);
        return success('ADD_SUCCESS', [ 'id' => $id ]);
    }

    /**
     * 商品编辑
     * @param $id  商品id
     * @return \think\Response
     */
    public function edit($id)
    {
        $data = $this->request->params([
            [ "goods_name", "" ],
            [ "sub_title", "" ],
            [ "goods_type", "" ],
            [ "goods_image", "" ],
            [ "goods_category", '' ],
            [ "brand_id", 0 ],
            [ "label_ids", "" ],
            [ 'service_ids', '' ],
            [ 'supplier_id', 0 ],
            [ "status", 0 ],
            [ "sort", 0 ],

            // 规格类型，single：单规格，multi：多规格
            [ 'spec_type', '' ],

            // 单规格数据
            [ "price", 0 ],
            [ "market_price", 0 ],
            [ "cost_price", 0 ],
            [ "price", 0 ],
            [ "stock", 0 ],
            [ "sku_no", '' ],
            [ "unit", "件" ],
            [ "virtual_sale_num", 0 ],

            // 多规格数据
            [ 'goods_spec_format', '' ],
            [ 'goods_sku_data', '' ],

            // 商品详情
            [ "goods_desc", "" ],
        ]);
        $this->validate($data, 'addon\shop\app\validate\goods\Goods.edit');
        $res = ( new VirtualGoodsService() )->edit($id, $data);
        return success('EDIT_SUCCESS');
    }

}
