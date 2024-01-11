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

namespace addon\shop\app\service\admin\goods;

use addon\shop\app\model\goods\Label;
use core\base\BaseAdminService;


/**
 * 商品标签服务层
 * Class LabelService
 * @package addon\shop\app\service\admin\goods
 */
class LabelService extends BaseAdminService
{
    public function __construct()
    {
        parent::__construct();
        $this->model = new Label();
    }

    /**
     * 获取商品标签列表
     * @param array $where
     * @return array
     */
    public function getPage(array $where = [])
    {
        $field = 'label_id,label_name,memo,sort,create_time,update_time';
        $order = 'create_time desc';

        $search_model = $this->model->where([ ['site_id', '=', $this->site_id] ])->withSearch([ "label_name" ], $where)->field($field)->order($order);
        $list = $this->pageQuery($search_model);
        return $list;
    }

    /**
     * 获取商品标签列表
     * @param array $where
     * @param string $field
     * @return array
     */
    public function getList(array $where = [], $field = 'label_id,label_name,memo,sort,create_time,update_time')
    {
        $order = "create_time desc";
        return $this->model->where([ ['site_id', '=', $this->site_id] ])->withSearch([ "label_name" ], $where)->field($field)->select()->order($order)->toArray();
    }

    /**
     * 获取商品标签信息
     * @param int $id
     * @return array
     */
    public function getInfo(int $id)
    {
        $field = 'label_id,label_name,memo,sort,create_time,update_time';

        $info = $this->model->field($field)->where([ [ 'label_id', '=', $id ],['site_id', '=', $this->site_id] ])->findOrEmpty()->toArray();
        return $info;
    }

    /**
     * 添加商品标签
     * @param array $data
     * @return mixed
     */
    public function add(array $data)
    {
        $data['site_id'] = $this->site_id;
        $data[ 'create_time' ] = time();
        $res = $this->model->create($data);
        return $res->label_id;

    }

    /**
     * 商品标签编辑
     * @param int $id
     * @param array $data
     * @return bool
     */
    public function edit(int $id, array $data)
    {
        $data[ 'update_time' ] = time();
        $this->model->where([ [ 'label_id', '=', $id ],['site_id', '=', $this->site_id] ])->update($data);
        return true;
    }

    /**
     * 删除商品标签
     * @param int $id
     * @return bool
     */
    public function del(int $id)
    {
        $model = $this->model->where([ [ 'label_id', '=', $id ],['site_id', '=', $this->site_id] ])->find();
        $res = $model->delete();
        return $res;
    }

}
