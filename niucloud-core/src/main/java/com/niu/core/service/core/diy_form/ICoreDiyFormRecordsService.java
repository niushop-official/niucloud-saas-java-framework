package com.niu.core.service.core.diy_form;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsSearchParam;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsInfoVo;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsListVo;

/**
 * 万能表单填写记录服务接口
 */
public interface ICoreDiyFormRecordsService {

    /**
     * 万能表单填写记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<DiyFormRecordsListVo>
     */
    PageResult<DiyFormRecordsListVo> page(PageParam pageParam, DiyFormRecordsSearchParam searchParam);

    /**
     * 万能表单填写记录详情
     * @param id 主键ID
     * @return  DiyFormRecordsInfoVo
     */
    DiyFormRecordsInfoVo info(Integer id);

    /**
     * 万能表单填写记录添加
     * @param addParam 添加参数
     */
    Integer add(DiyFormRecordsParam addParam);
    /**
     * 万能表单填写记录删除
     * @param id 主键ID
     */
    void del(Integer siteId, Integer id);

}
