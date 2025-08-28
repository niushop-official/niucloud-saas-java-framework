package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.entity.sys.SysSchedule;
import com.niu.core.entity.sys.SysScheduleLog;
import com.niu.core.service.admin.sys.param.SysScheduleLogSearchParam;
import com.niu.core.service.admin.sys.param.SysScheduleParam;
import com.niu.core.service.admin.sys.param.SysScheduleSearchParam;
import com.niu.core.service.admin.sys.param.SysScheduleStatusParam;
import com.niu.core.service.admin.sys.vo.SysScheduleInfoVo;
import com.niu.core.service.admin.sys.vo.SysScheduleListVo;
import com.niu.core.service.admin.sys.vo.SysScheduleLogListVo;
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

    /*
    * 清理计划任务日志
     */
    void deleteScheduleLog(long successThreshold, String status);

    /**
     * 重置计划任务
     * @return
     */
    void resetSchedule();

    /**
     * 计划任务执行记录分页列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<SysScheduleLogListVo> logList(PageParam pageParam, SysScheduleLogSearchParam searchParam);

    /**
     * 新增日志
     * @param log
     */
    void addLog(SysScheduleLog log);

    /**
     * 执行一次任务
     * @param id
     */
    void doSchedule(Integer id);

    /**
     * 删除日志
     * @param ids
     */
    void delLog(Integer[] ids);

    /**
     * 清空计划任务执行记录
     * @param scheduleId
     */
    void clearLog(Integer scheduleId);
}
