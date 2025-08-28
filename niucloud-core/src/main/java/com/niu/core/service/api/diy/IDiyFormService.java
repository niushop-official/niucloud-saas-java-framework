package com.niu.core.service.api.diy;

import com.niu.core.entity.diy_form.DiyFormWriteConfig;
import com.niu.core.service.api.diy.param.*;
import com.niu.core.service.api.diy.vo.DiyFormInfoVo;
import com.niu.core.service.api.diy.vo.DiyFormRecordsDetailVo;
import com.niu.core.service.api.diy.vo.DiyFormRecordsInfoVo;
import com.niu.core.service.api.diy.vo.DiyMemberRecordVo;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;

import java.util.Map;

public interface IDiyFormService {


    /**
     * 获取万能表单信息
     * @param formId
     * @return
     */
    DiyFormInfoVo getInfo(Integer formId);

    /**
     * 判断当前会员是否可以填写表单
     * @param memberId
     * @param diyFormWriteConfig
     * @return
     */
    Map<String, String> checkMemberCanJoinOrNot(Integer memberId, DiyFormWriteConfig diyFormWriteConfig);

    /**
     * 检查会员填写表单次数限制
     * @param memberId
     * @param formId
     * @param diyFormWriteConfig
     * @return
     */
    Map<String, String> checkMemberWriteLimitNum(Integer memberId, Integer formId, DiyFormWriteConfig diyFormWriteConfig);

    /**
     * 检查表单填写次数限制
     * @param formId
     * @param diyFormWriteConfig
     * @return
     */
    Map<String, String> checkFormWriteLimitNum(Integer formId, DiyFormWriteConfig diyFormWriteConfig);

    /**
     * 检查表单填写时间限制
     * @param diyFormWriteConfig
     * @return
     */
    Map<String, String> checkFormWriteTime(DiyFormWriteConfig diyFormWriteConfig);

    /**
     * 提交填表记录
     * @param diyFormRecordsParam
     */
    Integer addRecord(DiyFormRecordsParam diyFormRecordsParam);

    /**
     * 获取表单填写结果信息
     * @param recordId
     * @return
     */
    DiyFormRecordsInfoVo getResult(Integer recordId);

    /**
     * 获取表单填写记录，循环diy-group，每个表单组件实现各自的渲染处理
     * @param recordId
     * @return
     */
    DiyFormRecordsDetailVo getFormRecordInfo(Integer recordId);

    /**
     * 获取个人资料表单填写记录
     * @return
     */
    Object getMemberInfoRecord();

    /**
     * 修改表单填写记录
     * @param param
     */
    void editRecord(DiyFormRecordsParam param);
}
