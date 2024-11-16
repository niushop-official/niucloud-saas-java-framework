package com.niu.core.service.admin.addon.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.addon.param.AddonLogParam;
import com.niu.core.service.admin.addon.param.AddonLogSearchParam;
import com.niu.core.service.admin.addon.IAddonLogService;
import com.niu.core.service.admin.addon.vo.AddonLogInfoVo;
import com.niu.core.service.admin.addon.vo.AddonLogListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.addon.AddonLog;
import com.niu.core.mapper.addon.AddonLogMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * 插件日志实现类
 */
@Service
public class AddonLogServiceImpl implements IAddonLogService {

    @Resource
    AddonLogMapper addonLogMapper;

    /**
     * 插件日志列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<AddonLogListVo>
     */
    @Override
    public PageResult<AddonLogListVo> list(PageParam pageParam, AddonLogSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<AddonLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<AddonLog> iPage = addonLogMapper.selectPage(new Page<AddonLog>(page, limit), queryWrapper);

        List<AddonLogListVo> list = new LinkedList<>();
        for (AddonLog item : iPage.getRecords()) {
            AddonLogListVo vo = new AddonLogListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page,limit, iPage.getTotal()).setData(list);
    }

    /**
     * 插件日志详情
     *
     * @param id 主键参数
     * @return AddonLog
     */
    @Override
    public AddonLogInfoVo detail(Integer id) {
        AddonLog model = addonLogMapper.selectOne(
                new QueryWrapper<AddonLog>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        AddonLogInfoVo vo = new AddonLogInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 插件日志新增
     *
     * @param AddonLogParam 参数
     */
    @Override
    public void add(AddonLogParam AddonLogParam) {
        AddonLog model = new AddonLog();
        model.setAction(AddonLogParam.getAction());
        model.setKey(AddonLogParam.getKey());
        model.setFromVersion(AddonLogParam.getFromVersion());
        model.setToVersion(AddonLogParam.getToVersion());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        addonLogMapper.insert(model);
    }

    /**
     * 插件日志删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        AddonLog model = addonLogMapper.selectOne(
                new QueryWrapper<AddonLog>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        addonLogMapper.delete(new QueryWrapper<AddonLog>().eq("id", id));
    }

}
