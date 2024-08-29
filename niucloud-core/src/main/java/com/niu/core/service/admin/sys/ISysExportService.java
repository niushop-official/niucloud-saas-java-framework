package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysExportSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.vo.SysExportListVo;

import java.util.Map;

/**
 * 导出报服务接口
 */
public interface ISysExportService {


    /**
     * 导出报列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysExportListVo>
     */
    PageResult<SysExportListVo> list(PageParam pageParam, SysExportSearchParam searchParam);

    /**
     * 导出报删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 获取导出数据类型列表
     *
     * @return
     */
    Map<String, String> getExportDataType();

    /**
     * 检查导出数据源是否为空
     * @param type
     * @param whereMap
     * @return
     */
    Boolean checkExportData(String type, Map<String, Object> whereMap);

    /**
     * 导出数据
     *
     * @param type
     * @param whereMap
     */
    void exportData(String type, Map<String, Object> whereMap);

}
