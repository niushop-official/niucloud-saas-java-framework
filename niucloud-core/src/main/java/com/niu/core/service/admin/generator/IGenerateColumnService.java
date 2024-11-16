package com.niu.core.service.admin.generator;

import com.niu.core.entity.generator.GenerateColumn;

import java.util.List;

/**
 * 商品规格服务接口
 */
public interface IGenerateColumnService {

    void insertAll(List<GenerateColumn> list);

}
