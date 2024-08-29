package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysSchedule;
import com.niu.core.service.admin.sys.param.SysScheduleParam;
import com.niu.core.service.admin.sys.param.SysScheduleSearchParam;
import com.niu.core.service.admin.sys.param.SysScheduleStatusParam;
import com.niu.core.service.admin.sys.vo.SysScheduleInfoVo;
import com.niu.core.service.admin.sys.vo.SysScheduleListVo;
import com.niu.core.service.admin.sys.vo.SysScheduleTemplateVo;

import java.util.List;
import java.util.Map;

/**
 * 系统任务服务接口
 */
public interface ISysScheduleService {

    /**
     * 返回系统中需要启动的任务
     *
     * @return
     */
    public List<SysSchedule> getSysEnableList();

    /**
     * 系统任务列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysScheduleListVo>
     */
    PageResult<SysScheduleListVo> list(PageParam pageParam, SysScheduleSearchParam searchParam);

    /**
     * 系统任务详情
     *
     * @param id 主键ID
     * @return SysScheduleInfoVo
     */
    SysScheduleInfoVo info(Integer id);

    /**
     * @param sysScheduleStatusParam
     */
    void modifyStatus(SysScheduleStatusParam sysScheduleStatusParam);

    /**
     * 系统任务添加
     *
     * @param addParam 添加参数
     */
    void add(SysScheduleParam addParam);

    /**
     * 系统任务编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SysScheduleParam editParam);

    /**
     * 系统任务删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 任务类型
     *
     * @return
     */
    Map<String, String> type();

    /**
     * 模板
     *
     * @return
     */
    List<SysScheduleTemplateVo> template();

    /**
     * 数据类型
     *
     * @return
     */
    Map<String, String> dateType();

}
