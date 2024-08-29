package com.niu.core.service.core.notice;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysNoticeLog;
import com.niu.core.service.core.notice.param.NoticeLogSearchParam;

/**
 * 消息发送日志接口
 */
public interface ICoreNoticeLogService {

    Integer add(SysNoticeLog sysNoticeLog);

    PageResult<SysNoticeLog> getPage(Integer siteId, PageParam pageParam, NoticeLogSearchParam noticeLogSearchParam);

    SysNoticeLog getInfo(Integer siteId, Integer id);

}
