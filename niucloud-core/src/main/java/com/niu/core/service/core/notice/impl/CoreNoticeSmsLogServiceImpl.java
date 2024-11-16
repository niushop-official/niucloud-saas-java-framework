package com.niu.core.service.core.notice.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysNoticeSmsLog;
import com.niu.core.mapper.sys.SysNoticeSmsLogMapper;
import com.niu.core.service.admin.sys.param.SysNoticeSmsLogSearchParam;
import com.niu.core.service.core.notice.ICoreNoticeSmsLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreNoticeSmsLogServiceImpl implements ICoreNoticeSmsLogService {

    @Resource
    SysNoticeSmsLogMapper sysNoticeSmsLogMapper;

    @Override
    public PageResult<SysNoticeSmsLog> list(PageParam pageParam, SysNoticeSmsLogSearchParam searchParam) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysNoticeSmsLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<SysNoticeSmsLog> iPage = sysNoticeSmsLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    @Override
    public SysNoticeSmsLog info(Integer id) {
        SysNoticeSmsLog model = sysNoticeSmsLogMapper.selectOne(
                new QueryWrapper<SysNoticeSmsLog>()
                        .eq("id", id));
        return model;
    }

    @Override
    public void add(SysNoticeSmsLog addParam) {
        sysNoticeSmsLogMapper.insert(addParam);
    }

    @Override
    public void edit(Integer id, SysNoticeSmsLog editParam) {
        sysNoticeSmsLogMapper.updateById(editParam);
    }

    @Override
    public void del(Integer id) {
        sysNoticeSmsLogMapper.deleteById(id);
    }

}
