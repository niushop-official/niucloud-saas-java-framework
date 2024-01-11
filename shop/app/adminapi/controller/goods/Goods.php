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

use core\base\BaseAdminController;
use addon\shop\app\service\admin\goods\GoodsService;


/**
 * 商品控制器
 * Class Goods
 * @package addon\shop\app\adminapi\controller\goods
 */
class Goods extends BaseAdminController
{

    /**
     * 获取商品列表
     * @return \think\Response
     */
    public function pages()
    {
        $data = $this->request->params([
            [ "goods_name", "" ],
            [ "goods_category", '' ],
            [ "goods_type", "" ],
            [ "brand_id", "" ],
            [ "label_ids", "" ],
            [ "start_sale_num", "" ],
            [ "end_sale_num", "" ],
            [ "start_price", "" ],
            [ "end_price", "" ],
            [ "status", "" ],
            [ 'order', '' ],
            [ 'sort', '' ]
        ]);
        return success(( new GoodsService() )->getPage($data));
    }

    /**
     * 商品详情
     * @param int $id
     * @return \think\Response
     */
    public function info(int $id)
    {
        return success(( new GoodsService() )->getInfo($id));
    }

    /**
     * 获取商品添加/编辑数据
     * @return \think\Response
     */
    public function init()
    {
        $data = $this->request->params([
            [ "goods_id", 0 ],
        ]);
        return success(( new GoodsService() )->getInit($data));
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
            [ "weight", 0 ],
            [ "volume", 0 ],
            [ "stock", 0 ],
            [ "sku_no", '' ],
            [ "unit", "件" ],
            [ "virtual_sale_num", 0 ],

            // 多规格数据
            [ 'goods_spec_format', '' ],
            [ 'goods_sku_data', '' ],

            // 配送设置
            [ "delivery_type", "" ],
            [ "is_free_shipping", 0 ],
            [ 'fee_type', '' ],
            [ 'delivery_money', 0 ],
            [ "delivery_template_id", 0 ],

            // 商品详情
            [ "goods_desc", "" ],
        ]);

        $this->validate($data, 'addon\shop\app\validate\goods\Goods.add');
        $id = ( new GoodsService() )->add($data);
        return success('ADD_SUCCESS', [ 'id' => $id ]);
    }

    /**
     * 商品编辑
     * @param $id
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
            [ "weight", 0 ],
            [ "volume", 0 ],
            [ "stock", 0 ],
            [ "sku_no", '' ],
            [ "unit", "件" ],
            [ "virtual_sale_num", 0 ],

            // 多规格数据
            [ 'goods_spec_format', '' ],
            [ 'goods_sku_data', '' ],

            // 配送设置
            [ "delivery_type", "" ],
            [ "is_free_shipping", 0 ],
            [ 'fee_type', '' ],
            [ 'delivery_money', 0 ],
            [ "delivery_template_id", 0 ],

            // 商品详情
            [ "goods_desc", "" ],
        ]);
        $this->validate($data, 'addon\shop\app\validate\goods\Goods.edit');
        $res = ( new GoodsService() )->edit($id, $data);
        return success('EDIT_SUCCESS');
    }

    /**
     * 商品删除
     * @return \think\Response
     */
    public function del()
    {
        $data = $this->request->params([
            [ 'goods_ids', '' ],
        ]);
        ( new GoodsService() )->del($data[ 'goods_ids' ]);
        return success('DELETE_SUCCESS');
    }

    /**
     * 商品恢复
     * @return \think\Response
     */
    public function recycle()
    {
        $data = $this->request->params([
            [ 'goods_ids', '' ],
        ]);
        ( new GoodsService() )->recycle($data[ 'goods_ids' ]);
        return success('DELETE_SUCCESS');
    }

    /**
     * 获取商品类型
     * @return \think\Response
     */
    public function type()
    {
        return success(( new GoodsService() )->getType());
    }

    /**
     * 修改商品排序号
     * @return \think\Response
     */
    public function editSort()
    {
        $data = $this->request->params([
            [ 'goods_id', '' ],
            [ 'sort', '' ],
        ]);
        ( new GoodsService() )->editSort($data);
        return success('SUCCESS');
    }

    /**
     * 修改商品上下架状态
     * @return \think\Response
     */
    public function editStatus()
    {
        $data = $this->request->params([
            [ 'goods_ids', '' ],
            [ 'status', '' ],
        ]);
        ( new GoodsService() )->editStatus($data);
        return success('SUCCESS');
    }

    /**
     * 复制商品
     * @param int $goods_id
     * @return \think\Response
     */
    public function copy(int $goods_id)
    {
        ( new GoodsService() )->copy($goods_id);
        return success('SUCCESS');
    }

    /**
     * 获取回收站商品分页列表
     * @return \think\Response
     */
    public function recyclePages()
    {
        $data = $this->request->params([
            [ "goods_name", "" ],
            [ "goods_category", '' ],
            [ "goods_type", "" ],
        ]);
        return success(( new GoodsService() )->getRecyclePage($data));
    }

    /**
     * 商品选择分页列表
     * @return \think\Response
     */
    public function select()
    {
        $data = $this->request->params([
            [ 'keyword', '' ], // 搜索关键词
            [ "goods_category", "" ], // 商品分类
            [ "goods_type", "" ], // 商品分类
            [ 'goods_ids', '' ], // 已选商品id集合
            [ 'verify_goods_ids', '' ] // 检测商品id集合是否存在，移除不存在的商品id，纠正数据准确性
        ]);

        return success(( new GoodsService() )->getSelectPage($data));
    }

    /**
     * 查询商品SKU规格列表
     * @return \think\Response
     */
    public function sku()
    {
        $data = $this->request->params([
            [ 'goods_id', '' ]
        ]);

        return success(( new GoodsService() )->getSkuList($data));
    }

    /**
     * 编辑商品规格列表库存
     * @return \think\Response
     */
    public function editGoodsListStock()
    {
        $data = $this->request->params([
            [ 'goods_id', 0 ],
            [ 'sku_list', '' ]
        ]);
        ( new GoodsService() )->editGoodsListStock($data);
        return success('EDIT_SUCCESS');
    }

    /**
     * 编辑商品规格列表库存
     * @return \think\Response
     */
    public function editGoodsListPrice()
    {
        $data = $this->request->params([
            [ 'goods_id', 0 ],
            [ 'sku_list', '' ]
        ]);
        ( new GoodsService() )->editGoodsListPrice($data);
        return success('EDIT_SUCCESS');
    }

}
