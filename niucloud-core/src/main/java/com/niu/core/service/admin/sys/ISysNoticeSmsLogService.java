package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysNoticeSmsLogParam;
import com.niu.core.service.admin.sys.param.SysNoticeSmsLogSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.vo.SysNoticeSmsLogInfoVo;
import com.niu.core.service.admin.sys.vo.SysNoticeSmsLogListVo;

/**
 * 短信发送服务接口
 */
public interface ISysNoticeSmsLogService {


    /**
     * 短信发送列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysNoticeSmsLogListVo>
     */
    PageResult<SysNoticeSmsLogListVo> list(PageParam pageParam, SysNoticeSmsLogSearchParam searchParam);

    /**
     * 短信发送详情
     * @param id 主键ID
     * @return SysNoticeSmsLogInfoVo
     */
    SysNoticeSmsLogInfoVo info(Integer id);


}
