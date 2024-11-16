package com.niu.core.service.admin.generator.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.entity.generator.GenerateColumn;
import com.niu.core.mapper.generator.GenerateColumnMapper;
import com.niu.core.service.admin.generator.IGenerateColumnService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GenerateColumnServiceImpl extends ServiceImpl<GenerateColumnMapper, GenerateColumn> implements IGenerateColumnService {

    @Resource
    GenerateColumnMapper generateColumnMapper;

    @Override
    public void insertAll(List<GenerateColumn> list) {
        super.saveOrUpdateBatch(list);
    }
}
