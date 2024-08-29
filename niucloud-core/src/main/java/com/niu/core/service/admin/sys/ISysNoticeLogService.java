package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysNoticeLogParam;
import com.niu.core.service.admin.sys.param.SysNoticeLogSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.vo.SysNoticeLogInfoVo;
import com.niu.core.service.admin.sys.vo.SysNoticeLogListVo;

/**
 * 通知记录服务接口
 */
public interface ISysNoticeLogService {


    /**
     * 通知记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysNoticeLogListVo>
     */
    PageResult<SysNoticeLogListVo> list(PageParam pageParam, SysNoticeLogSearchParam searchParam);

    /**
     * 通知记录详情
     * @param id 主键ID
     * @return SysNoticeLogInfoVo
     */
    SysNoticeLogInfoVo info(Integer id);

}
