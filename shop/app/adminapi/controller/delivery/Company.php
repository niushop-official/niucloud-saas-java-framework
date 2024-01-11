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

namespace addon\shop\app\adminapi\controller\delivery;

use core\base\BaseAdminController;
use addon\shop\app\service\admin\delivery\CompanyService;


/**
 * 物流公司控制器
 * Class Company
 * @package addon\shop\app\adminapi\controller\delivery
 */
class Company extends BaseAdminController
{
   /**
    * 获取物流公司列表
    * @return \think\Response
    */
    public function lists(){
        $data = $this->request->params([
             ["company_name",""],
        ]);
        return success((new CompanyService())->getPage($data));
    }

    /**
     * 物流公司详情
     * @param int $id
     * @return \think\Response
     */
    public function info(int $id){
        return success((new CompanyService())->getInfo($id));
    }

    /**
     * 添加物流公司
     * @return \think\Response
     */
    public function add(){
        $data = $this->request->params([
             ["company_name",""],
             ["logo",""],
             ["url",""],
             ["express_no",""],
        ]);
        $this->validate($data, 'addon\shop\app\validate\delivery\Company.add');
        $id = (new CompanyService())->add($data);
        return success('ADD_SUCCESS', ['id' => $id]);
    }

    /**
     * 物流公司编辑
     * @param $id  物流公司id
     * @return \think\Response
     */
    public function edit($id){
        $data = $this->request->params([
             ["company_name",""],
             ["logo",""],
             ["url",""],
             ["express_no",""],
        ]);
        $this->validate($data, 'addon\shop\app\validate\delivery\Company.edit');
        (new CompanyService())->edit($id, $data);
        return success('EDIT_SUCCESS');
    }

    /**
     * 物流公司删除
     * @param $id  物流公司id
     * @return \think\Response
     */
    public function del(int $id){
        (new CompanyService())->del($id);
        return success('DELETE_SUCCESS');
    }


}
