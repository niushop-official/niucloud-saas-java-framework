package com.niu.core.service.admin.stat;

import com.niu.core.service.admin.stat.vo.StatInfoVo;

/**
 * 统计数据服务层
 */
public interface IStatService {

    /**
     * 首页统计数据
     * @return
     */
    StatInfoVo getIndexData();

}
