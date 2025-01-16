package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.sys.ExportEnum;
import com.niu.core.service.admin.sys.ISysExportService;
import com.niu.core.service.admin.sys.param.SysExportSearchParam;
import com.niu.core.service.admin.sys.vo.SysExportListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 导出报控制器
 */

@RestController
@RequestMapping("adminapi/sys/")
@SaCheckLogin
public class SysExportController {

    @Resource
    ISysExportService sysExportService;

    /**
     * 导出报列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < SysExportListVo>>
     */
    @GetMapping("/export")
    public Result<PageResult<SysExportListVo>> list(@Validated PageParam pageParam,
                                                    @Validated SysExportSearchParam searchParam) {
        PageResult<SysExportListVo> list = sysExportService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取导出状态列表
     */
    @GetMapping("/export/status")
    public Result<Object> getExportStatus() {
        return Result.success(ExportEnum.getStatus());
    }

    /**
     * 报表导出类型
     */
    @GetMapping("/export/type")
    public Result<Map<String, String>> getExportDataType() {
        return Result.success(sysExportService.getExportDataType());
    }

    /**
     * 报表导出数据检查
     */
    @GetMapping("/export/check/{type}")
    public Result<Object> check(@Validated @PathVariable("type") String type, @RequestParam Map<String, Object> objectMap) {
        return Result.success(sysExportService.checkExportData(type, objectMap));
    }

    /**
     * 报表导出
     */
    @GetMapping("/export/{type}")
    public Result<Object> export(@Validated @PathVariable("type") String type, @RequestParam Map<String, Object> jsonObject) {
        sysExportService.exportData(type, jsonObject);
        return Result.success();
    }

    /**
     * 导出报删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/export/{id}")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        sysExportService.del(id);
        return Result.success();
    }

}
