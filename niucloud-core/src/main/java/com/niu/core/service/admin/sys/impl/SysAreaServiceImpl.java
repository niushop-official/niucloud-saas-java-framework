package com.niu.core.service.admin.sys.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.mapper.sys.SysAreaMapper;
import com.niu.core.service.admin.sys.ISysAreaService;
import com.niu.core.service.admin.sys.param.SysAreaSearchParam;
import com.niu.core.service.admin.sys.vo.SysAreaListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 地址实现
 */
@Service
public class SysAreaServiceImpl implements ISysAreaService {

    @Resource
    SysAreaMapper sysAreaMapper;

    @Override
    public List<SysArea> getListByPid(Integer pid) {

        QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", pid);
        return sysAreaMapper.selectList(queryWrapper);
    }

    @Override
    public JSONArray getAreaTree(Integer level) {
        QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("level", level);
        List<SysArea> list = sysAreaMapper.selectList(queryWrapper);
        JSONArray jsonArray = JSONUtil.parseArray(JSONUtil.toJsonStr(list));
        return TreeUtils.listToTree(jsonArray, "id", "pid", "child");
    }

    @Override
    public JSONArray getAreaByAreaCode(Integer id) {
        SysArea areaInfo = sysAreaMapper.selectOne(new QueryWrapper<SysArea>().eq("id", id).last("limit 1"));

        return null;
    }

    @Override
    public Integer getAreaId(String name, Integer level) {
        SysArea areaInfo = sysAreaMapper.selectOne(new QueryWrapper<SysArea>().like("name", name).eq("level", level).last("limit 1"));
        if (areaInfo != null) {
            return areaInfo.getId();
        }
        return null;
    }

    @Override
    public String getAreaName(Integer id) {
        SysArea areaInfo = sysAreaMapper.selectOne(new QueryWrapper<SysArea>().eq("id", id).last("limit 1"));
        if (areaInfo != null) {
            return areaInfo.getName();
        }
        return null;
    }

    /**
     * 地址列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysAreaListVo>
     */
    @Override
    public PageResult<SysAreaListVo> list(PageParam pageParam, SysAreaSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(Arrays.asList("sort", "id"));

        IPage<SysArea> iPage = sysAreaMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysAreaListVo> list = new LinkedList<>();
        for (SysArea item : iPage.getRecords()) {
            SysAreaListVo vo = new SysAreaListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit).setData(list);
    }

}
