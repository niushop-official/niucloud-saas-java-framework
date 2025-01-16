package com.niu.core.service.core.sys;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.sys.SysExport;
import com.niu.core.service.core.sys.param.SysExportParam;

import java.util.Map;

/**
 * 导出服务器底层
 */
public interface ICoreExportService {

    /**
     * 添加导出记录
     *
     * @param sysExport
     */
    void add(SysExport sysExport);

    /**
     * 编辑导出记录
     *
     * @param exportId
     * @param sysExportParam
     */
    void edit(Integer exportId, SysExportParam sysExportParam);

    /**
     * 获取导出数据类型列表
     *
     * @return
     */
    Map<String, String> getExportDataType();

    /**
     * 获取导出数据列
     *
     * @param type
     * @return
     */
    JSONArray getExportDataColumn(String type);

    /**
     * 获取导出数据源
     *
     * @param siteId
     * @param type
     * @param where
     * @param pageParam
     * @return
     */
    JSONArray getExportData(Integer siteId, String type, JSONObject where, PageParam pageParam);

    /**
     * 导出数据
     *
     * @param siteId
     * @param dataKey
     * @param dataColumn
     * @param data
     */
    void export(Integer siteId, Integer exportId, String dataKey, JSONArray dataColumn, JSONArray data);

    /**
     * 删除导出报表文件
     *
     * @param filePath
     */
    void deleteExportFile(String filePath);
}
