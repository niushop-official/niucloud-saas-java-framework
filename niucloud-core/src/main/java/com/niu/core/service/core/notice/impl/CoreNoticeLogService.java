package com.niu.core.service.core.notice.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.sys.SysAttachment;
import com.niu.core.entity.sys.SysNoticeLog;
import com.niu.core.mapper.sys.SysNoticeLogMapper;
import com.niu.core.service.admin.sys.vo.SysAttachmentListVo;
import com.niu.core.service.core.notice.ICoreNoticeLogService;
import com.niu.core.service.core.notice.param.NoticeLogSearchParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
public class CoreNoticeLogService implements ICoreNoticeLogService {

    @Resource
    SysNoticeLogMapper sysNoticeLogMapper;

    @Override
    public Integer add(SysNoticeLog sysNoticeLog) {
        sysNoticeLogMapper.insert(sysNoticeLog);
        return sysNoticeLog.getId();
    }

    @Override
    public PageResult<SysNoticeLog> getPage(Integer siteId, PageParam pageParam, NoticeLogSearchParam noticeLogSearchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<SysNoticeLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        if (ObjectUtil.isNotEmpty(noticeLogSearchParam.getKey())) queryWrapper.eq("key", noticeLogSearchParam.getKey());
        if (ObjectUtil.isNotEmpty(noticeLogSearchParam.getReceiver())) queryWrapper.eq("receiver", noticeLogSearchParam.getReceiver());
        if (ObjectUtil.isNotEmpty(noticeLogSearchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", noticeLogSearchParam.getCreateTime());

        IPage<SysNoticeLog> iPage = sysNoticeLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    @Override
    public SysNoticeLog getInfo(Integer siteId, Integer id) {
        return sysNoticeLogMapper.selectOne(new QueryWrapper<SysNoticeLog>().eq("id", id).eq("site_id", siteId));
    }
}
