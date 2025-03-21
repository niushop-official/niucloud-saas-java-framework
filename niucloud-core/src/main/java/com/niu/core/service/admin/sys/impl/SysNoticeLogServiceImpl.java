package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.sys.SysNoticeLog;
import com.niu.core.enums.notice.NoticeEnum;
import com.niu.core.enums.notice.vo.NoticeEnumListVo;
import com.niu.core.mapper.sys.SysNoticeLogMapper;
import com.niu.core.service.admin.sys.param.SysNoticeLogParam;
import com.niu.core.service.admin.sys.param.SysNoticeLogSearchParam;
import com.niu.core.service.admin.sys.ISysNoticeLogService;
import com.niu.core.service.admin.sys.vo.SysNoticeLogInfoVo;
import com.niu.core.service.admin.sys.vo.SysNoticeLogListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 通知记录实现
 */
@Service
public class SysNoticeLogServiceImpl implements ISysNoticeLogService {
        
    @Resource
    SysNoticeLogMapper sysNoticeLogMapper;

    /**
     * 通知记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysNoticeLogListVo>
     */
    @Override
    public PageResult<SysNoticeLogListVo> list(PageParam pageParam, SysNoticeLogSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<SysNoticeLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getReceiver())) queryWrapper.eq("receiver", searchParam.getReceiver());
        if (ObjectUtil.isNotEmpty(searchParam.getKey())) queryWrapper.eq("`key`", searchParam.getKey());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        queryWrapper.orderByDesc("id");

        Map<String, NoticeEnumListVo> noticeEnum = NoticeEnum.getNotice();

        IPage<SysNoticeLog> iPage = sysNoticeLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysNoticeLogListVo> list = new LinkedList<>();
        for (SysNoticeLog item : iPage.getRecords()) {
            SysNoticeLogListVo vo = new SysNoticeLogListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setName(noticeEnum.get(item.getKey()) != null ? noticeEnum.get(item.getKey()).getName() : "");
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 通知记录详情
     * @param id 主键
     * @return SysNoticeLogInfoVo
     */
    @Override
    public SysNoticeLogInfoVo info(Integer id) {
        SysNoticeLog model = sysNoticeLogMapper.selectOne(
                new QueryWrapper<SysNoticeLog>()
                    .eq("id", id)
                    .eq("site_id", RequestUtils.siteId())
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        SysNoticeLogInfoVo vo = new SysNoticeLogInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

}
