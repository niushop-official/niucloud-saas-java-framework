package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.sys.SysUpgradeRecords;
import com.niu.core.enums.sys.UpgradeRecordStatusEnum;
import com.niu.core.mapper.sys.SysUpgradeRecordsMapper;
import com.niu.core.service.admin.sys.ISysUpgradeRecordsService;
import com.niu.core.service.admin.sys.param.SysUpgradeRecordsDelParam;
import com.niu.core.service.admin.sys.param.SysUpgradeRecordsParam;
import com.niu.core.service.admin.sys.param.SysUpgradeRecordsSearchParam;
import com.niu.core.service.admin.sys.vo.SysUpgradeRecordsListVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SysUpgradeRecordsServiceImpl implements ISysUpgradeRecordsService {
    private final String STATUS_READY = "ready";
    private final String STATUS_FAIL = "fail";
    @Resource
    private SysUpgradeRecordsMapper sysUpgradeRecordsMapper;

    @Override
    public PageResult<SysUpgradeRecordsListVo> page(PageParam pageParam, SysUpgradeRecordsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysUpgradeRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        if (searchParam != null) {
            if (searchParam.getName() != null) {
                queryWrapper.like("name", searchParam.getName());
            }
        }

        IPage<SysUpgradeRecords> iPage = sysUpgradeRecordsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysUpgradeRecordsListVo> list = new ArrayList<>();
        for (SysUpgradeRecords item : iPage.getRecords()) {
            SysUpgradeRecordsListVo vo = new SysUpgradeRecordsListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setCreateTime(DateUtils.timestampToString(item.getCreateTime()));
            vo.setCompleteTime(DateUtils.timestampToString(item.getCompleteTime()));
            vo.setStatusName(UpgradeRecordStatusEnum.getNameByStatus(vo.getStatus()));
            //判断为数组或者对象或者字符串
            String value = item.getContent();
            if (!StrUtil.isEmpty(value)) {
                if (value.startsWith("[")) {
                    vo.setContent(JSON.parseArray(value));
                } else if (value.startsWith("{")) {
                    vo.setContent(JSON.parseObject(value));
                } else {
                    vo.setContent(value);
                }
            }
            list.add(vo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public void add(SysUpgradeRecordsParam addParam) {
        SysUpgradeRecords model = new SysUpgradeRecords();
        BeanUtils.copyProperties(addParam, model);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        sysUpgradeRecordsMapper.insert(model);
    }

    @Override
    public void edit(String key, SysUpgradeRecordsParam editParam) {
        SysUpgradeRecords model = new SysUpgradeRecords();
        BeanUtils.copyProperties(editParam, model);
        sysUpgradeRecordsMapper.update(model, new QueryWrapper<SysUpgradeRecords>().eq("upgrade_key", key));
    }

    @Override
    public void clear(long threshold) {
        sysUpgradeRecordsMapper.delete(new QueryWrapper<SysUpgradeRecords>()
                .lt("create_time", threshold).in("status", STATUS_READY, STATUS_FAIL));
    }

    @Override
    public void del(SysUpgradeRecordsDelParam delParam) {
        QueryWrapper queryWrapper = new QueryWrapper<SysUpgradeRecords>();

        if (delParam.getIds().getClass().getName().startsWith("[")) {
            String[] stringIds = (String[]) delParam.getIds();
            queryWrapper.in("id", stringIds);
        } else {
            queryWrapper.eq("id", delParam.getIds());
        }
        sysUpgradeRecordsMapper.delete(queryWrapper);
    }
}
