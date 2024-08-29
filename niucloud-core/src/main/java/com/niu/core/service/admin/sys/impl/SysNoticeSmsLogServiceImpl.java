package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.sys.SysNoticeSmsLog;
import com.niu.core.mapper.sys.SysNoticeSmsLogMapper;
import com.niu.core.service.admin.sys.param.SysNoticeSmsLogParam;
import com.niu.core.service.admin.sys.param.SysNoticeSmsLogSearchParam;
import com.niu.core.service.admin.sys.ISysNoticeSmsLogService;
import com.niu.core.service.admin.sys.vo.SysNoticeSmsLogInfoVo;
import com.niu.core.service.admin.sys.vo.SysNoticeSmsLogListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 短信发送实现
 */
@Service
public class SysNoticeSmsLogServiceImpl implements ISysNoticeSmsLogService {
        
    @Resource
    SysNoticeSmsLogMapper sysNoticeSmsLogMapper;

    /**
     * 短信发送列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysNoticeSmsLogListVo>
     */
    @Override
    public PageResult<SysNoticeSmsLogListVo> list(PageParam pageParam, SysNoticeSmsLogSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<SysNoticeSmsLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getKey())) queryWrapper.eq("`key`", searchParam.getKey());
        if (ObjectUtil.isNotEmpty(searchParam.getSmsType())) queryWrapper.eq("sms_type", searchParam.getSmsType());
        if (ObjectUtil.isNotEmpty(searchParam.getMobile())) queryWrapper.eq("mobile", searchParam.getMobile());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper,"create_time" ,searchParam.getCreateTime());
        queryWrapper.orderByDesc("id");

        IPage<SysNoticeSmsLog> iPage = sysNoticeSmsLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysNoticeSmsLogListVo> list = new LinkedList<>();
        for (SysNoticeSmsLog item : iPage.getRecords()) {
            SysNoticeSmsLogListVo vo = new SysNoticeSmsLogListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit).setData(list);
    }

    /**
     * 短信发送详情
     * @param id 主键
     * @return SysNoticeSmsLogInfoVo
     */
    @Override
    public SysNoticeSmsLogInfoVo info(Integer id) {
        SysNoticeSmsLog model = sysNoticeSmsLogMapper.selectOne(
                new QueryWrapper<SysNoticeSmsLog>()
                    .eq("id", id)
                    .eq("site_id", RequestUtils.siteId())
                    .last("limit 1"));
        if (model == null) return null;

        SysNoticeSmsLogInfoVo vo = new SysNoticeSmsLogInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }
}
