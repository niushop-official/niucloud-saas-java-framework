package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.sys.SysExport;
import com.niu.core.enums.sys.ExportEnum;
import com.niu.core.mapper.sys.SysExportMapper;
import com.niu.core.service.admin.sys.ISysExportService;
import com.niu.core.service.admin.sys.param.SysExportSearchParam;
import com.niu.core.service.admin.sys.vo.SysExportListVo;
import com.niu.core.service.core.sys.ICoreExportService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.Map;


/**
 * 导出报实现
 */
@Service
public class SysExportServiceImpl implements ISysExportService {

    @Resource
    SysExportMapper sysExportMapper;

    @Resource
    ICoreExportService coreExportService;

    /**
     * 导出报列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysExportListVo>
     */
    @Override
    public PageResult<SysExportListVo> list(PageParam pageParam, SysExportSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysExport> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.like("export_key", searchParam.getExportKey());

        if (ObjectUtil.isNotEmpty(searchParam.getExportKey())) {
            queryWrapper.eq("export_status", searchParam.getExportStatus());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            String[] createTime = searchParam.getCreateTime();
            long startTime = (createTime[0] == null) ? 0 : DateUtils.StringToTimestamp(createTime[0]);
            long endTime = (createTime[1] == null) ? 0 : DateUtils.StringToTimestamp(createTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between("create_time", startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge("create_time", startTime);
            } else if (startTime == 0 && endTime > 0) {
                queryWrapper.le("create_time", startTime);
            }
        }

        JSONArray results = JsonModuleLoader.build().mergeResultSet("export/ExportType.json");

        IPage<SysExport> iPage = sysExportMapper.selectPage(new Page<>(page, limit), queryWrapper);
        IPage<SysExportListVo> converted = iPage.convert(export -> {
            SysExportListVo vo = new SysExportListVo();
            BeanUtils.copyProperties(export, vo);
            vo.setCreateTime(DateUtils.timestampToString(export.getCreateTime()));
            vo.setExportStatusName(ExportEnum.getNameByCode(export.getExportStatus()));
            results.stream()
                    .map(JSONUtil::parseObj)
                    .filter(o -> o.containsKey(export.getExportKey()))
                    .findFirst()
                    .map(o -> o.getJSONObject(export.getExportKey()))
                    .ifPresent(o -> vo.setExportKeyName(o.getStr("name")));

            return vo;
        });

        return PageResult.build(converted);
    }

    /**
     * 导出报删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysExport model = sysExportMapper.selectOne(
                new QueryWrapper<SysExport>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        sysExportMapper.delete(new QueryWrapper<SysExport>().eq("id", id));
    }

    /**
     * 获取导出数据类型列表
     */
    @Override
    public Map<String, String> getExportDataType() {
        return coreExportService.getExportDataType();
    }

    /**
     * 检查导出数据源是否为空
     */
    @Override
    public Boolean checkExportData(String type, Map<String, Object> whereMap) {
        PageParam pageParam = new PageParam();
        pageParam.setPage(Integer.parseInt(whereMap.getOrDefault("page", 0).toString()));
        pageParam.setLimit(Integer.parseInt(whereMap.getOrDefault("limit", 0).toString()));
        JSONArray jsonArray = coreExportService.getExportData(RequestUtils.siteId(), type, JSONUtil.parseObj(whereMap), pageParam);
        return !jsonArray.isEmpty();
    }

    /**
     * 导出数据
     */
    @Override
    public void exportData(String type, Map<String, Object> whereMap) {
        Integer siteId = RequestUtils.siteId();
        PageParam pageParam = new PageParam();
        pageParam.setPage((Integer) whereMap.getOrDefault("page", 0));
        pageParam.setLimit((Integer) whereMap.getOrDefault("limit", 0));

        JSONArray dataColumn = coreExportService.getExportDataColumn(type);
        JSONArray dataArray = coreExportService.getExportData(siteId, type, JSONUtil.parseObj(whereMap), pageParam);

        SysExport export = new SysExport();
        export.setSiteId(RequestUtils.siteId());
        export.setExportKey(type);
        export.setExportNum(CollectionUtil.size(dataArray));
        export.setCreateTime(System.currentTimeMillis() / 1000);
        coreExportService.add(export);

        coreExportService.export(siteId, export.getId(), type, dataColumn, dataArray);
    }

}
