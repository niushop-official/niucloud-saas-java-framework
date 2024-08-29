package com.niu.core.service.admin.stat;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.stat.param.StatHourParam;
import com.niu.core.service.admin.stat.param.StatHourSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.stat.vo.StatHourInfoVo;
import com.niu.core.service.admin.stat.vo.StatHourListVo;

/**
 * 小时统计服务接口
 */
public interface IStatHourService {


    /**
     * 小时统计列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<StatHourListVo>
     */
    PageResult<StatHourListVo> list(PageParam pageParam, StatHourSearchParam searchParam);

    /**
     * 小时统计详情
     * @param id 主键ID
     * @return StatHourInfoVo
     */
    StatHourInfoVo info(Integer id);

    /**
     * 小时统计添加
     * @param addParam 添加参数
     */
    void add(StatHourParam addParam);

    /**
     * 小时统计编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, StatHourParam editParam);

    /**
     * 小时统计删除
     * @param id 主键ID
     */
    void del(Integer id);

}
