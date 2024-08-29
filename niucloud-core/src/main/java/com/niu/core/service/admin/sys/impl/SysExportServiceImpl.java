package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.sys.SysExport;
import com.niu.core.mapper.sys.SysExportMapper;
import com.niu.core.service.admin.sys.param.SysExportSearchParam;
import com.niu.core.service.admin.sys.ISysExportService;
import com.niu.core.service.admin.sys.vo.SysExportInfoVo;
import com.niu.core.service.admin.sys.vo.SysExportListVo;
import com.niu.core.service.core.sys.ICoreExportService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
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
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysExportListVo>
     */
    @Override
    public PageResult<SysExportListVo> list(PageParam pageParam, SysExportSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<SysExport> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.like("export_key", searchParam.getExportKey());
        queryWrapper.eq("export_status", searchParam.getExportStatus());

        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            String[] createTime = searchParam.getCreateTime();
            Long startTime = (createTime[0] == null) ? 0: DateUtils.StringToTimestamp(createTime[0]);
            Long endTime = (createTime[1] == null) ? 0: DateUtils.StringToTimestamp(createTime[1]);
            if(startTime > 0 && endTime > 0){
                queryWrapper.between("create_time", startTime, endTime);
            }else if (startTime > 0 && endTime == 0){
                queryWrapper.ge("create_time", startTime);
            }else if (startTime == 0 && endTime > 0){
                queryWrapper.le("create_time", startTime);
            }
        }

        IPage<SysExportListVo> iPage = sysExportMapper.selectJoinPage(new Page<>(page, limit), SysExportListVo.class,queryWrapper);
        return PageResult.build(page, limit).setData(iPage.getRecords());
    }

    /**
     * 导出报删除
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
     *
     * @return
     */
    @Override
    public Map<String, String> getExportDataType() {
        return coreExportService.getExportDataType();
    }

    /**
     * 检查导出数据源是否为空
     *
     * @param type
     * @param whereMap
     * @return
     */
    @Override
    public Boolean checkExportData(String type, Map<String, Object> whereMap) {
        Integer siteId= RequestUtils.siteId();
        JSONObject whereJson=new JSONObject();
        whereJson.set("keyword", whereMap.getOrDefault("keyword", ""));
        whereJson.set("register_type", whereMap.getOrDefault("register_type", ""));
        whereJson.set("create_time", whereMap.getOrDefault("create_time", ""));
        whereJson.set("is_del", whereMap.getOrDefault("is_del", ""));
        whereJson.set("member_label", whereMap.getOrDefault("member_label", ""));
        whereJson.set("register_channel", whereMap.getOrDefault("register_channel", ""));
        whereJson.set("member_level", whereMap.getOrDefault("member_level", ""));

        PageParam pageParam=new PageParam();
        pageParam.setPage(Integer.parseInt(whereMap.getOrDefault("page", 0).toString()));
        pageParam.setLimit(Integer.parseInt(whereMap.getOrDefault("limit", 0).toString()));
        JSONArray jsonArray=coreExportService.getExportData(siteId, type, whereJson, pageParam);
        return jsonArray.size()>0;
    }

    /**
     * 导出数据
     *
     * @param type
     * @param whereMap
     */
    @Override
    public void exportData(String type, Map<String, Object> whereMap) {
        Integer siteId= RequestUtils.siteId();
        JSONObject whereJson=new JSONObject();
        whereJson.set("keyword", whereMap.getOrDefault("keyword", ""));
        whereJson.set("register_type", whereMap.getOrDefault("register_type", ""));
        whereJson.set("create_time", whereMap.getOrDefault("create_time", ""));
        whereJson.set("is_del", whereMap.getOrDefault("is_del", ""));
        whereJson.set("member_label", whereMap.getOrDefault("member_label", ""));
        whereJson.set("register_channel", whereMap.getOrDefault("register_channel", ""));
        whereJson.set("member_level", whereMap.getOrDefault("member_level", ""));

        PageParam pageParam=new PageParam();
        pageParam.setPage((Integer) whereMap.getOrDefault("page", 0));
        pageParam.setLimit((Integer) whereMap.getOrDefault("limit", 0));

        JSONArray dataColumn=coreExportService.getExportDataColumn(type);
        JSONArray dataArray=coreExportService.getExportData(siteId, type, whereJson, pageParam);
        coreExportService.export(siteId, type, dataColumn, dataArray);
    }

}
