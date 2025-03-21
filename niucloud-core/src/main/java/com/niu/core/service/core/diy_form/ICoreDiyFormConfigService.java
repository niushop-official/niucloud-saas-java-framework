package com.niu.core.service.core.diy_form;

import com.niu.core.service.core.diy_form.param.DiyFormSubmitConfigParam;
import com.niu.core.service.core.diy_form.vo.DiyFormSubmitConfigInfoVo;
import com.niu.core.service.core.diy_form.param.DiyFormWriteConfigParam;
import com.niu.core.service.core.diy_form.vo.DiyFormWriteConfigInfoVo;;

/**
 * 万能表单填写配置服务接口
 */
public interface ICoreDiyFormConfigService {

    /**
     * 获取万能表单填写配置详情
     * @param siteId
     * @param formId
     * @return
     */
    DiyFormWriteConfigInfoVo getWriteConfig(Integer siteId, Integer formId);

    /**
     * 万能表单填写配置添加
     * @param addParam 添加参数
     */
    void addWriteConfig(DiyFormWriteConfigParam addParam);

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
    DiyFormSubmitConfigInfoVo getSubmitConfig(Integer siteId, Integer formId);

    /**
     * 万能表单提交页配置添加
     * @param addParam 添加参数
     */
    void addSubmitConfig(DiyFormSubmitConfigParam addParam);

    /**
     * 万能表单提交页配置编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void editSubmitConfig(DiyFormSubmitConfigParam editParam);


}
