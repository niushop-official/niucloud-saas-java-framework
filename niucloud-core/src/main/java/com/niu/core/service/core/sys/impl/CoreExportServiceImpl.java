package com.niu.core.service.core.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.Export.BusinessExcelUtil;
import com.niu.core.common.utils.Export.ExportDynamic;
import com.niu.core.common.utils.Export.ExportHeads;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.sys.SysExport;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.sys.SysExportMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.sys.ICoreExportService;
import com.niu.core.service.core.sys.param.SysExportParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * 导出服务层
 *
 */
@Service
public class CoreExportServiceImpl implements ICoreExportService {

    @Resource
    SysExportMapper sysExportMapper;
    /**
     * 添加导出记录
     *
     * @param sysExport
     */
    @Override
    @Transactional
    public void add(SysExport sysExport) {
        sysExportMapper.insert(sysExport);
    }

    /**
     * 编辑导出记录
     *
     * @param exportId
     * @param sysExportParam
     */
    @Override
    public void edit(Integer exportId, SysExportParam sysExportParam) {
        SysExport sysExport=sysExportMapper.selectById(exportId);

        Assert.notNull(sysExport, "数据不存在");

        sysExport.setExportKey(sysExportParam.getExportKey());
        sysExport.setExportNum(sysExportParam.getExportNum());
        sysExport.setFailReason(sysExportParam.getFailReason());
        sysExport.setFilePath(sysExportParam.getFilePath());
        sysExport.setFailReason(sysExportParam.getFailReason());
        sysExportMapper.updateById(sysExport);
    }


    /**
     * 获取导出数据类型列表
     *
     * @return
     */
    @Override
    public Map<String, String> getExportDataType() {
        Map<String, String> dataType=new HashMap<>();
        JSONArray jsonArray=JsonModuleLoader.build().mergeResultSet("export/ExportType.json");
        for (Object obj:jsonArray) {
            JSONObject jsonObject=(JSONObject) obj;
            Set<String> setString=jsonObject.keySet();
            for (String key: setString) {
                JSONObject values=jsonObject.getJSONObject(key);
                dataType.put(key, values.getStr("name"));
            }
        }
        return dataType;
    }

    /**
     * 获取导出数据列
     *
     * @param type
     * @return
     */
    @Override
    public JSONArray getExportDataColumn(String type) {
        JSONArray exportDataColumn=JsonModuleLoader.build().mergeResultSet("export/ExportType.json");
        if(ObjectUtil.isNotNull(type) && ObjectUtil.isNotEmpty(type)){
            JSONArray typeColumn=new JSONArray();
            for (Object obj:exportDataColumn) {
                JSONObject jsonObject=(JSONObject) obj;
                Set<String> setString=jsonObject.keySet();
                for (String key: setString) {
                    if(key.equals(type)){
                        JSONObject values=jsonObject.getJSONObject(key);
                        typeColumn=values.getJSONArray("column");
                    }
                }
            }
            return typeColumn;
        }
        return exportDataColumn;
    }

    /**
     * 获取导出数据源
     *
     * @param siteId
     * @param type
     * @param where
     * @param pageParam
     * @return
     */
    @Override
    public JSONArray getExportData(Integer siteId, String type, JSONObject where, PageParam pageParam) {
        ExportDataEventDefiner.ExportDataEvent exportDataEvent=new ExportDataEventDefiner.ExportDataEvent();
        exportDataEvent.setSiteId(siteId);
        exportDataEvent.setType(type);
        exportDataEvent.setWhere(where);
        exportDataEvent.setPageParam(pageParam);
        List<ExportDataEventDefiner.ExportDataEventResult> resultList= EventAndSubscribeOfPublisher.publishAndCallback(exportDataEvent);
        JSONArray exportResult=new JSONArray();
        for (ExportDataEventDefiner.ExportDataEventResult itemResult:resultList){
            if(itemResult.getResultData().size()>0){
                exportResult.addAll(itemResult.getResultData());
            }
        }
        return exportResult;
    }

    /**
     * 导出数据
     *
     * @param siteId
     * @param dataType
     * @param headColumn
     * @param valueData
     */
    @Override
    public void export(Integer siteId, String dataType, JSONArray headColumn, JSONArray valueData) {
        ExportDynamic exportDynamic=new ExportDynamic();
        String fileName=dataType+"_"+ DateUtils.currTime()+".xlsx";
        exportDynamic.setFileName(fileName);
        String filePath=WebAppEnvs.get().webRootDownResource+"/upload"+"/export/";
        exportDynamic.setFilePath(filePath);
        List<ExportHeads> headsList=new ArrayList<>();
        List<String> keyList=new ArrayList<>();
        for (Object headObj: headColumn) {
            JSONObject itemObject=(JSONObject) headObj;
            String itemKey=itemObject.keySet().stream().iterator().next();
            JSONObject itemValue=itemObject.getJSONObject(itemKey);
            String titleHead=itemValue.getStr("name");
            ExportHeads exportHeads=new ExportHeads();
            exportHeads.setLabel(titleHead);
            headsList.add(exportHeads);
            keyList.add(itemKey);
        }
        exportDynamic.setHeads(headsList);
        List<List<Object>> datas=new ArrayList<>();
        for (Object itemObj: valueData) {
            JSONObject itemJsonObject=(JSONObject) itemObj;
            List<Object> itemDatas=new ArrayList<>();
            for (String itemKey:keyList) {
                itemDatas.add(itemJsonObject.getStr(itemKey, ""));
            }
            datas.add(itemDatas);
        }
        exportDynamic.setDatas(datas);
        BusinessExcelUtil.exportBusinessExcel(exportDynamic);
    }

    /**
     * 删除导出报表文件
     *
     * @param filePath
     */
    @Override
    public void deleteExportFile(String filePath) {

    }
}
