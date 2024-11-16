package com.niu.core.service.admin.dict;


import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.dict.param.DictDataParam;
import com.niu.core.service.admin.dict.param.DictParam;
import com.niu.core.service.admin.dict.param.DictSearchParam;
import com.niu.core.service.admin.dict.vo.DictInfoVo;
import com.niu.core.service.admin.dict.vo.DictListVo;

import java.util.List;

/**
 * 字典数据服务接口类
 */
public interface IDictService {


    /**
     * 字典列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<DictListVo> getPage(PageParam pageParam, DictSearchParam searchParam);

    /**
     * 字典数据详情
     * @param id 主键ID
     * @return DictInfoVo
     */
    DictInfoVo info(Integer id);

    /**
     * 字典数据详情
     * @param key 关键字
     * @return DictInfoVo
     */
    DictInfoVo info(String key);

    /**
     * 字典数据添加
     * @param addParam 参数
     */
    void add(DictParam addParam);

    /**
     * 字典数据编辑
     * @param id 参数
     * @param editParam 参数
     */
    void edit(Integer id, DictParam editParam);

    /**
     * 添加字典数据
     * @param id
     * @param dictDataParam
     */
    void addDictData(Integer id, DictDataParam dictDataParam);

    /**
     * 字典数据删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 所有字典列表
     * @return
     */
    List<DictListVo> getAll();



}
