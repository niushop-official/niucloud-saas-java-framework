package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysUpgradeRecordsDelParam;
import com.niu.core.service.admin.sys.param.SysUpgradeRecordsParam;
import com.niu.core.service.admin.sys.param.SysUpgradeRecordsSearchParam;
import com.niu.core.service.admin.sys.vo.SysUpgradeRecordsListVo;

public interface ISysUpgradeRecordsService {

    /**
     * 升级记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysUpgradeRecordsListVo>
     */
    PageResult<SysUpgradeRecordsListVo> page(PageParam pageParam, SysUpgradeRecordsSearchParam searchParam);

    /**
     * 升级记录添加
     * @param addParam 添加参数
     */
    void add(SysUpgradeRecordsParam addParam);

    /**
     * 升级记录编辑
     * @param key   关键字
     * @param editParam 编辑参数
     */
    void edit(String key, SysUpgradeRecordsParam editParam);

    /**
     * 清除
     * @param threshold
     */
    void clear(long threshold);

    void del(SysUpgradeRecordsDelParam delParam);
}
