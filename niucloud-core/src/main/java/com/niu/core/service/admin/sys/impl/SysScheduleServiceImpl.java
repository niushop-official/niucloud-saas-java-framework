package com.niu.core.service.admin.sys.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.component.job.factory.JobProviderFactory;
import com.niu.core.common.component.job.model.JobInfo;
import com.niu.core.common.component.job.quartz.QuartzJobManager;
import com.niu.core.entity.sys.SysSchedule;
import com.niu.core.mapper.sys.SysScheduleMapper;
import com.niu.core.service.admin.sys.ISysScheduleService;
import com.niu.core.service.admin.sys.param.SysScheduleParam;
import com.niu.core.service.admin.sys.param.SysScheduleSearchParam;
import com.niu.core.service.admin.sys.param.SysScheduleStatusParam;
import com.niu.core.service.admin.sys.vo.SysScheduleInfoVo;
import com.niu.core.service.admin.sys.vo.SysScheduleListVo;
import com.niu.core.service.admin.sys.vo.SysScheduleTemplateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * 系统任务实现
 */
@Service
@Slf4j
public class SysScheduleServiceImpl implements ISysScheduleService {

    @Resource
    private SysScheduleMapper sysScheduleMapper;

    @Override
    public List<SysSchedule> getSysEnableList() {
        QueryWrapper<SysSchedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("sort", "id");
        return sysScheduleMapper.selectList(queryWrapper);
    }

    /**
     * 系统任务列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysScheduleListVo>
     */
    @Override
    public PageResult<SysScheduleListVo> list(PageParam pageParam, SysScheduleSearchParam searchParam) {
        // 分页参数
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        // 构造查询条件执行查询
        QueryWrapper<SysSchedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(Arrays.asList("sort", "id"));
        IPage<SysSchedule> sysSchedulePage = sysScheduleMapper.selectPage(new Page<>(page, limit), queryWrapper);
        // 返回结果集
        List<SysScheduleListVo> sysScheduleListVoList = new LinkedList<>();
        for (SysSchedule sysSchedule : sysSchedulePage.getRecords()) {
            SysScheduleListVo sysScheduleListVo = new SysScheduleListVo();
            BeanUtils.copyProperties(sysSchedule, sysScheduleListVo);
            String jobKey = sysScheduleListVo.getKey();
            JobInfo jobInfo = JobProviderFactory.getJobInfo(jobKey);
            if (jobInfo != null) {
                sysScheduleListVo.setName(jobInfo.getName());
            }
            sysScheduleListVo.setTimeObject(sysSchedule.getTime());
            sysScheduleListVo.setCrontabContent(QuartzJobManager.convertCronContent(sysSchedule.getTime()));
            sysScheduleListVoList.add(sysScheduleListVo);
        }
        return PageResult.build(page, limit, sysSchedulePage.getTotal()).setData(sysScheduleListVoList);
    }

    /**
     * @param id
     * @return
     */
    private SysSchedule findByIdAndCheck(Integer id) {
        SysSchedule sysSchedule = sysScheduleMapper.selectById(id);
        Assert.notNull(sysSchedule, "系统任务不存在");
        return sysSchedule;
    }

    /**
     * 系统任务详情
     *
     * @param id 主键
     * @return SysScheduleInfoVo
     */
    @Override
    public SysScheduleInfoVo info(Integer id) {
        SysSchedule sysSchedule = sysScheduleMapper.selectById(id);
        SysScheduleInfoVo sysScheduleInfoVo = new SysScheduleInfoVo();
        BeanUtils.copyProperties(sysSchedule, sysScheduleInfoVo);
        return sysScheduleInfoVo;
    }

    @Override
    public void modifyStatus(SysScheduleStatusParam sysScheduleStatusParam) {
        SysSchedule oldSysSchedule = this.findByIdAndCheck(sysScheduleStatusParam.getId());
        if (sysScheduleStatusParam.getStatus() == 2) {
            QuartzJobManager.stopJob(oldSysSchedule);
        } else {
            QuartzJobManager.stopJob(oldSysSchedule);
            QuartzJobManager.startJob(oldSysSchedule);
        }
        SysSchedule newSysSchedule = new SysSchedule();
        newSysSchedule.setId(oldSysSchedule.getId());
        newSysSchedule.setStatus(sysScheduleStatusParam.getStatus());
        newSysSchedule.setUpdateTime(DateUtils.currTime());
        sysScheduleMapper.updateById(newSysSchedule);
    }

    /**
     * 系统任务添加
     *
     * @param sysScheduleParam 参数
     */
    @Override
    public void add(SysScheduleParam sysScheduleParam) {
        String jobKey = sysScheduleParam.getKey();
        JobInfo jobInfo = JobProviderFactory.getJobInfo(jobKey);
        if (jobInfo == null) {
            throw new RuntimeException("任务不存在.");
        }
        // 校验任务是否存在
        QueryWrapper<SysSchedule> queryWrapper = new QueryWrapper();
        queryWrapper.eq("`key`", jobKey);
        List<SysSchedule> sysScheduleList = sysScheduleMapper.selectList(queryWrapper);
        if (sysScheduleList != null && sysScheduleList.size() > 0) {
            throw new RuntimeException("任务已经存在.");
        }
        // 新增一个任务
        SysSchedule newSysSchedule = new SysSchedule();
        BeanUtils.copyProperties(sysScheduleParam, newSysSchedule);
        newSysSchedule.setTime(JSONUtil.toJsonStr(sysScheduleParam.getTime()));
        newSysSchedule.setCreateTime(DateUtils.currTime());
        newSysSchedule.setUpdateTime(DateUtils.currTime());
        newSysSchedule.setSiteId(RequestUtils.adminSiteId());
        newSysSchedule.setAddon(jobInfo.getSource());
        int result = sysScheduleMapper.insert(newSysSchedule);
        if (result > 0) {
            if (newSysSchedule.getStatus() == 1) {
                // 如果是启动状态，就启动任务
                QuartzJobManager.startJob(newSysSchedule);
            }
        }
    }

    /**
     * 系统任务编辑
     *
     * @param sysScheduleParam 参数
     */
    @Override
    public void edit(Integer id, SysScheduleParam sysScheduleParam) {
        // 存在性检查
        this.findByIdAndCheck(id);
        // 构建更新对象
        SysSchedule newSysSchedule = new SysSchedule();
        BeanUtils.copyProperties(sysScheduleParam, newSysSchedule);
        newSysSchedule.setId(id);
        newSysSchedule.setSiteId(RequestUtils.adminSiteId());
        newSysSchedule.setUpdateTime(DateUtils.currTime());
        newSysSchedule.setTime(JSONUtil.toJsonStr(sysScheduleParam.getTime()));
        int result = sysScheduleMapper.updateById(newSysSchedule);
        if (result <= 0) {
            log.error("更新系统任务失败：id={}", id);
        }
        // 根据状态启停任务
        if (sysScheduleParam.getStatus() == 2) {
            QuartzJobManager.stopJob(newSysSchedule.getKey());
        } else {
            QuartzJobManager.stopJob(newSysSchedule.getKey());
            QuartzJobManager.startJob(newSysSchedule);
        }
    }

    /**
     * 系统任务删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysSchedule sysSchedule = this.findByIdAndCheck(id);
        if (sysSchedule.getStatus() == 1) {
            QuartzJobManager.stopJob(sysSchedule);
        }
        sysScheduleMapper.deleteById(id);
    }

    @Override
    public List<SysScheduleTemplateVo> template() {
        List<SysScheduleTemplateVo> sysScheduleTemplateVoList = new ArrayList<>();
        Set<String> jobKeys = JobProviderFactory.jobKeys();
        for (String jobKey : jobKeys) {
            JobInfo jobInfo = JobProviderFactory.getJobInfo(jobKey);
            SysScheduleTemplateVo sysScheduleTemplateVo = new SysScheduleTemplateVo();
            sysScheduleTemplateVo.setKey(jobKey);
            sysScheduleTemplateVo.setName(jobInfo.getName());
            sysScheduleTemplateVo.setDesc(jobInfo.getDescribe());
            sysScheduleTemplateVo.setClazz(jobInfo.getJobClass());
            sysScheduleTemplateVo.setFunction(jobInfo.getJobClass());
            sysScheduleTemplateVoList.add(sysScheduleTemplateVo);
        }
        return sysScheduleTemplateVoList;
    }

    @Override
    public Map<String, String> type() {
        Map<String, String> typeData = new HashMap<>();
        typeData.put("cron", "定时任务");
        typeData.put("crond", "周期任务");
        return typeData;
    }

    @Override
    public Map<String, String> dateType() {
        Map<String, String> dateTypeData = new HashMap<>();
        dateTypeData.put("min", "每隔几分钟");
        dateTypeData.put("hour", "每隔几小时");
        dateTypeData.put("day", "每隔几天");
        dateTypeData.put("week", "每周");
        dateTypeData.put("month", "每月");
        return dateTypeData;
    }

}
