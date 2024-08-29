package com.niu.core.service.admin.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.sys.SysPoster;
import com.niu.core.mapper.sys.SysPosterMapper;
import com.niu.core.service.admin.sys.param.SysPosterParam;
import com.niu.core.service.admin.sys.param.SysPosterSearchParam;
import com.niu.core.service.admin.sys.ISysPosterService;
import com.niu.core.service.admin.sys.vo.SysPosterInfoVo;
import com.niu.core.service.admin.sys.vo.SysPosterListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 海报实现
 */
@Service
public class SysPosterServiceImpl implements ISysPosterService {
        
    @Resource
    SysPosterMapper sysPosterMapper;

    /**
     * 海报列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysPosterListVo>
     */
    @Override
    public PageResult<SysPosterListVo> list(PageParam pageParam, SysPosterSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<SysPoster> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<SysPoster> iPage = sysPosterMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysPosterListVo> list = new LinkedList<>();
        for (SysPoster item : iPage.getRecords()) {
            SysPosterListVo vo = new SysPosterListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit).setData(list);
    }

    /**
     * 海报详情
     * @param id 主键
     * @return SysPosterInfoVo
     */
    @Override
    public SysPosterInfoVo info(Integer id) {
        SysPoster model = sysPosterMapper.selectOne(
                new QueryWrapper<SysPoster>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        SysPosterInfoVo vo = new SysPosterInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 海报添加
     * @param addParam 参数
     */
    @Override
    public void add(SysPosterParam addParam) {
        SysPoster model = new SysPoster();
        model.setSiteId(addParam.getSiteId());
        model.setName(addParam.getName());
        model.setType(addParam.getType());
        model.setChannel(addParam.getChannel());
        model.setValue(addParam.getValue());
        model.setStatus(addParam.getStatus());
        model.setAddon(addParam.getAddon());
        model.setIsDefault(addParam.getIsDefault());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        sysPosterMapper.insert(model);
    }

    /**
     * 海报编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, SysPosterParam editParam) {
        SysPoster model = sysPosterMapper.selectOne(
                new QueryWrapper<SysPoster>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        model.setSiteId(editParam.getSiteId());
        model.setName(editParam.getName());
        model.setType(editParam.getType());
        model.setChannel(editParam.getChannel());
        model.setValue(editParam.getValue());
        model.setStatus(editParam.getStatus());
        model.setAddon(editParam.getAddon());
        model.setIsDefault(editParam.getIsDefault());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        sysPosterMapper.updateById(model);
    }

    /**
     * 海报删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysPoster model = sysPosterMapper.selectOne(
                new QueryWrapper<SysPoster>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        sysPosterMapper.delete(new QueryWrapper<SysPoster>().eq("id", id));
    }

}
