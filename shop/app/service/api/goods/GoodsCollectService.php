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

namespace addon\shop\app\service\api\goods;

use addon\shop\app\model\goods\GoodsCollect;
use core\base\BaseApiService;
use core\exception\CommonException;

/**
 *  商品收藏服务层
 */
class GoodsCollectService extends BaseApiService
{
    public function __construct()
    {
        parent::__construct();
        $this->model = new GoodsCollect();
    }

    /**
     * 商品收藏列表
     */
    public function getMemberGoodsCollectList()
    {
        $search_model = $this->model->where([ [ 'member_id', '=', $this->member_id ], [ 'site_id', '=', $this->site_id ] ])->with('goods')->order('id desc');
        $list = $this->pageQuery($search_model);
        return $list;
    }

    /**
     * 商品添加收藏
     */
    public function addGoodsCollect($data)
    {
        $data[ 'member_id' ] = $this->member_id;
        $data[ 'site_id' ] = $this->site_id;
        $info = $this->model->where([
            [ 'member_id', '=', $data[ 'member_id' ] ],
            [ 'goods_id', '=', $data[ 'goods_id' ] ],
            [ 'site_id', '=', $this->site_id ]
        ])->findOrEmpty()->toArray();

        if (!empty($info)) {
            throw new CommonException('MEMBER_ALREADY_COLLECT');//已收藏
        } else {
            // 添加
            $data[ 'create_time' ] = time();
            $res = $this->model->create($data);
            return $res->id;
        }
    }

    /**
     * 商品取消收藏
     */
    public function cancelGoodsCollect($data)
    {
        $res = $this->model->where([ [ 'goods_id', '=', $data[ 'goods_id' ] ], [ 'member_id', '=', $this->member_id ], [ 'site_id', '=', $this->site_id ] ])->delete();
        return $res;
    }
}
