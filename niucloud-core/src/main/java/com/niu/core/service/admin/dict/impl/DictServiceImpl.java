package com.niu.core.service.admin.dict.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysDict;
import com.niu.core.mapper.sys.SysDictMapper;
import com.niu.core.service.admin.dict.IDictService;
import com.niu.core.service.admin.dict.param.DictDataParam;
import com.niu.core.service.admin.dict.param.DictParam;
import com.niu.core.service.admin.dict.param.DictSearchParam;
import com.niu.core.service.admin.dict.vo.DictInfoVo;
import com.niu.core.service.admin.dict.vo.DictListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 字典数据实现类
 */
@Service
public class DictServiceImpl implements IDictService {
        
    @Resource
    SysDictMapper dictMapper;


    @Override
    public PageResult<DictListVo> getPage(PageParam pageParam, DictSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        //查询条件判断组装
        if (ObjectUtil.isNotEmpty(searchParam.getName())) {
            queryWrapper.like("name", searchParam.getName());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getKey())) {
            queryWrapper.eq("`key`", searchParam.getKey());
        }

       queryWrapper.orderByDesc("id");

        IPage<SysDict> iPage = dictMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<DictListVo> list = new LinkedList<>();
        for (SysDict item : iPage.getRecords()) {
            DictListVo vo = new DictListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 字典数据详情
     *
     * @param id 主键参数
     * @return DictData
     */
    @Override
    public DictInfoVo info(Integer id) {
        SysDict model = dictMapper.selectOne(
                new QueryWrapper<SysDict>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        DictInfoVo vo = new DictInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public DictInfoVo info(String key) {
        SysDict model = dictMapper.selectOne(
                new QueryWrapper<SysDict>()
                        .eq("`key`", key)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        DictInfoVo vo = new DictInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 字典数据新增
     *
     * @param addParam 参数
     */
    @Override
    public void add(DictParam addParam) {
        SysDict model = new SysDict();
        model.setName(addParam.getName());
        model.setKey(addParam.getKey());
        model.setMemo(addParam.getMemo());
        model.setDictionary("[]");
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        dictMapper.insert(model);
    }

    /**
     * 字典数据编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, DictParam editParam) {
        SysDict model = dictMapper.selectOne(
                new QueryWrapper<SysDict>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        model.setId(id);
        model.setName(editParam.getName());
        model.setKey(editParam.getKey());
        model.setMemo(editParam.getMemo());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        dictMapper.updateById(model);
    }

    @Override
    public void addDictData(Integer id, DictDataParam dictDataParam)
    {
        SysDict model = dictMapper.selectOne(
                new QueryWrapper<SysDict>()
                        .eq("id",  id)
                        .last("limit 1"));
        model.setDictionary(dictDataParam.getDictionary());
        dictMapper.updateById(model);
    }

    /**
     * 字典数据删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysDict model = dictMapper.selectOne(
                new QueryWrapper<SysDict>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        dictMapper.deleteById(model);
    }

    @Override
    public List<DictListVo> getAll() {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();


        queryWrapper.orderByDesc("id");

        List<SysDict> voList = dictMapper.selectList(queryWrapper);
        List<DictListVo> list = new LinkedList<>();
        for (SysDict item : voList) {
            DictListVo vo = new DictListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

}
