package com.niu.core.service.admin.dict;

import com.niu.core.service.admin.dict.param.DictDataParam;
import com.niu.core.service.admin.dict.vo.DictDataInfoVo;

/**
 * 字典数据服务接口类
 */
public interface IDictDataService {

    /**
     * 字典数据详情
     *
     * @param id 主键ID
     * @return DictDataInfoVo
     */
    DictDataInfoVo info(Integer id);

    /**
     * 字典数据添加
     *
     * @param addParam 参数
     */
    void add(DictDataParam addParam);

    /**
     * 字典数据编辑
     *
     * @param id 参数
     * @param editParam 参数
     */
    void edit(Integer id, DictDataParam editParam);

    /**
     * 字典数据删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

}
