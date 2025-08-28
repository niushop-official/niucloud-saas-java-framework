package com.niu.core.service.admin.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysUserLog;
import com.niu.core.service.admin.sys.param.SysUserLogParam;
import com.niu.core.service.admin.sys.param.SysUserLogSearchParam;
import com.niu.core.service.admin.sys.vo.SysUserLogInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserLogListVo;
import com.niu.core.common.domain.PageParam;

/**
 * 管理员操作记录服务接口类
 */
public interface ISysUserLogService extends IService<SysUserLog> {

    /**
     * 管理员操作记录列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysUserLogListedVo>
     */
    PageResult<SysUserLogListVo> list(PageParam pageParam, SysUserLogSearchParam searchParam);

    /**
     * 管理员操作记录详情
     *
     * @param id 主键ID
     * @return SysUserLogInfoVo
     */
    SysUserLogInfoVo info(Integer id);

    /**
     * 管理员操作记录删除
     *
     */
    void destroy();

}
