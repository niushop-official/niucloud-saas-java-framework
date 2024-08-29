package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysNoticeParam;
import com.niu.core.service.admin.sys.param.SysNoticeSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.vo.SysNoticeInfoVo;
import com.niu.core.service.admin.sys.vo.SysNoticeListVo;

/**
 * 通知模型服务接口
 */
public interface ISysNoticeService {


    /**
     * 通知模型列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysNoticeListVo>
     */
    PageResult<SysNoticeListVo> list(PageParam pageParam, SysNoticeSearchParam searchParam);

    /**
     * 通知模型详情
     * @param id 主键ID
     * @return SysNoticeInfoVo
     */
    SysNoticeInfoVo info(Integer id);

    /**
     * 通知模型添加
     * @param addParam 添加参数
     */
    void add(SysNoticeParam addParam);

    /**
     * 通知模型编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SysNoticeParam editParam);

    /**
     * 通知模型删除
     * @param id 主键ID
     */
    void del(Integer id);

}
