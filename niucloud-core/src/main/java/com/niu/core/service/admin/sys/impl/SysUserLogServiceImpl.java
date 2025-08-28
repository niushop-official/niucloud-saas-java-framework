package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.sys.param.SysUserLogParam;
import com.niu.core.service.admin.sys.param.SysUserLogSearchParam;
import com.niu.core.service.admin.sys.ISysUserLogService;
import com.niu.core.service.admin.sys.vo.SysUserLogInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserLogListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.sys.SysUserLog;
import com.niu.core.mapper.sys.SysUserLogMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 管理员操作记录实现类
 */
@Service
public class SysUserLogServiceImpl extends ServiceImpl<SysUserLogMapper, SysUserLog> implements ISysUserLogService {

    @Resource
    SysUserLogMapper sysUserLogMapper;

    RedisTemplate<String, Object> redisTemplate;

    /**
     * 管理员操作记录列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysUserLogListVo>
     */
    @Override
    public PageResult<SysUserLogListVo> list(PageParam pageParam, SysUserLogSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysUserLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("id");
        if(ObjectUtil.isNotNull(searchParam.getUsername()) && ObjectUtil.isNotEmpty(searchParam.getUsername())){
            queryWrapper.like("username", searchParam.getUsername());
        }
        if(ObjectUtil.isNotNull(searchParam.getIp()) && ObjectUtil.isNotEmpty(searchParam.getIp())){
            queryWrapper.like("ip", searchParam.getIp());
        }

        if(ObjectUtil.isNotNull(searchParam.getUrl()) && ObjectUtil.isNotEmpty(searchParam.getUrl())){
            queryWrapper.like("url", searchParam.getUrl());
        }
        IPage<SysUserLog> iPage = sysUserLogMapper.selectPage(new Page<SysUserLog>(page, limit), queryWrapper);

        List<SysUserLogListVo> list = new LinkedList<>();
        for (SysUserLog item : iPage.getRecords()) {
            SysUserLogListVo vo = new SysUserLogListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 管理员操作记录详情
     *
     * @param id 主键参数
     * @return SysUserLog
     */
    @Override
    public SysUserLogInfoVo info(Integer id) {
        SysUserLog model = sysUserLogMapper.selectOne(
                new QueryWrapper<SysUserLog>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        SysUserLogInfoVo vo = new SysUserLogInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public void destroy() {
        sysUserLogMapper.delete(new QueryWrapper<SysUserLog>().eq("site_id", RequestUtils.siteId()));
    }
}
