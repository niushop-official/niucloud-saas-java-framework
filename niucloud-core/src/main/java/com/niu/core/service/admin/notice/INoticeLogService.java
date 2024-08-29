package com.niu.core.service.admin.notice;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysNoticeLog;
import com.niu.core.service.core.notice.param.NoticeLogSearchParam;

/**
 * 消息发送日志接口
 */
public interface INoticeLogService {

    PageResult<SysNoticeLog> getPage(PageParam pageParam, NoticeLogSearchParam noticeLogSearchParam);

    SysNoticeLog getInfo(Integer id);

}
