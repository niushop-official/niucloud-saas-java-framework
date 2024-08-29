package com.niu.core.service.admin.dict.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.service.admin.dict.param.DictDataParam;
import com.niu.core.service.admin.dict.vo.DictDataInfoVo;
import com.niu.core.entity.dict.DictData;
import com.niu.core.mapper.dict.DictDataMapper;
import com.niu.core.service.admin.dict.IDictDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;


/**
 * 字典数据实现类
 */
@Service
public class DictDataServiceImpl implements IDictDataService {
        
    @Resource
    DictDataMapper dictDataMapper;


    /**
     * 字典数据详情
     *
     * @param id 主键参数
     * @return DictData
     */
    @Override
    public DictDataInfoVo info(Integer id) {
        DictData model = dictDataMapper.selectOne(
                new QueryWrapper<DictData>()
                    .eq("id", id)
                    .eq("is_delete", 0)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        DictDataInfoVo vo = new DictDataInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 字典数据新增
     *
     * @param addParam 参数
     */
    @Override
    public void add(DictDataParam addParam) {
        DictData model = new DictData();
        model.setTypeId(addParam.getTypeId());
        model.setName(addParam.getName());
        model.setValue(addParam.getValue());
        model.setRemark(addParam.getRemark());
        model.setSort(addParam.getSort());
        model.setStatus(addParam.getStatus());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        dictDataMapper.insert(model);
    }

    /**
     * 字典数据编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, DictDataParam editParam) {
        DictData model = dictDataMapper.selectOne(
                new QueryWrapper<DictData>()
                    .eq("id",  id)
                    .eq("is_delete", 0)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        model.setId(id);
        model.setTypeId(editParam.getTypeId());
        model.setName(editParam.getName());
        model.setValue(editParam.getValue());
        model.setRemark(editParam.getRemark());
        model.setSort(editParam.getSort());
        model.setStatus(editParam.getStatus());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        dictDataMapper.updateById(model);
    }

    /**
     * 字典数据删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        DictData model = dictDataMapper.selectOne(
                new QueryWrapper<DictData>()
                    .eq("id", id)
                    .eq("is_delete", 0)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        model.setIsDelete(1);
        model.setDeleteTime(System.currentTimeMillis() / 1000);
        dictDataMapper.updateById(model);
    }

}
