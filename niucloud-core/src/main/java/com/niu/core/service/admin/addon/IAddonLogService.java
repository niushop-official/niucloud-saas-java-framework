package com.niu.core.service.admin.addon;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.addon.param.AddonLogParam;
import com.niu.core.service.admin.addon.param.AddonLogSearchParam;
import com.niu.core.service.admin.addon.vo.AddonLogInfoVo;
import com.niu.core.service.admin.addon.vo.AddonLogListVo;
import com.niu.core.common.domain.PageParam;

/**
 * 插件日志服务接口类
 */
public interface IAddonLogService {

    /**
     * 插件日志列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<AddonLogListVo>
     */
    PageResult<AddonLogListVo> list(PageParam pageParam, AddonLogSearchParam searchParam);

    /**
     * 插件日志详情
     *
     * @param id 主键ID
     * @return AddonLogInfoVo
     */
    AddonLogInfoVo detail(Integer id);

    /**
     * 插件日志新增
     *
     * @param AddonLogParam 参数
     */
    void add(AddonLogParam AddonLogParam);

    /**
     * 插件日志删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

}
