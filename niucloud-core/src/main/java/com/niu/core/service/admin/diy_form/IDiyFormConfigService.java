package com.niu.core.service.admin.diy_form;

import com.niu.core.service.core.diy_form.param.DiyFormSubmitConfigParam;
import com.niu.core.service.core.diy_form.param.DiyFormWriteConfigParam;
import com.niu.core.service.core.diy_form.vo.DiyFormSubmitConfigInfoVo;
import com.niu.core.service.core.diy_form.vo.DiyFormWriteConfigInfoVo;
/**
 * 万能表单填写配置服务接口
 */
public interface IDiyFormConfigService {

    /**
     * 获取万能表单填写配置详情
     * @param formId
     * @return
     */
    DiyFormWriteConfigInfoVo getWriteConfig(Integer formId);


    /**
     * 万能表单填写配置编辑
     * @param editParam 编辑参数
     */
    void editWriteConfig(DiyFormWriteConfigParam editParam);

    /**
     * 万能表单提交页配置详情
     * @param formId 主键ID
     * @return  DiyFormSubmitConfigInfoVo
     */
    DiyFormSubmitConfigInfoVo getSubmitConfig(Integer formId);

    /**
     * 万能表单提交页配置编辑
     * @param editParam 编辑参数
     */
    void editSubmitConfig(DiyFormSubmitConfigParam editParam);

}
