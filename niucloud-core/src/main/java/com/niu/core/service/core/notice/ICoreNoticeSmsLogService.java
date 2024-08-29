package com.niu.core.service.core.notice;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysNoticeSmsLog;
import com.niu.core.service.admin.sys.param.SysNoticeSmsLogSearchParam;

/**
 * 短信发送服务接口
 */
public interface ICoreNoticeSmsLogService {


    /**
     * 短信发送列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysNoticeSmsLog>
     */
    PageResult<SysNoticeSmsLog> list(PageParam pageParam, SysNoticeSmsLogSearchParam searchParam);

    /**
     * 短信发送详情
     * @param id 主键ID
     * @return SysNoticeSmsLog
     */
    SysNoticeSmsLog info(Integer id);

    /**
     * 短信发送添加
     * @param addParam 添加参数
     */
    void add(SysNoticeSmsLog addParam);

    /**
     * 短信发送编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SysNoticeSmsLog editParam);

    /**
     * 短信发送删除
     * @param id 主键ID
     */
    void del(Integer id);

}
