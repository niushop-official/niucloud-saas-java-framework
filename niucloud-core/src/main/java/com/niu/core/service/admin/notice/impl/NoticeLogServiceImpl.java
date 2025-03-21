package com.niu.core.service.admin.notice.impl;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.sys.SysNoticeLog;
import com.niu.core.service.admin.notice.INoticeLogService;
import com.niu.core.service.core.notice.ICoreNoticeLogService;
import com.niu.core.service.core.notice.param.NoticeLogSearchParam;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class NoticeLogServiceImpl implements INoticeLogService {

    @Resource
    ICoreNoticeLogService coreNoticeLogService;

    @Override
    public PageResult<SysNoticeLog> getPage(PageParam pageParam, NoticeLogSearchParam noticeLogSearchParam) {
        return coreNoticeLogService.getPage(RequestUtils.siteId(), pageParam, noticeLogSearchParam);
    }

    @Override
    public SysNoticeLog getInfo(Integer id) {
        return coreNoticeLogService.getInfo(RequestUtils.siteId(), id);
    }
}
