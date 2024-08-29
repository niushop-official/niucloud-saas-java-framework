package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.service.admin.sys.ISysAreaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 地址控制器
 */

@RestController
@RequestMapping("adminapi/sys/area")
@SaCheckLogin
public class SysAreaController {

    @Resource
    ISysAreaService sysAreaService;

    /**
     * 获取pid的子项列表
     *
     * @param pid
     * @return
     */
    @GetMapping("/list_by_pid/{pid}")
    public Result<List<SysArea>> getListByPid(@PathVariable("pid") Integer pid) {
        return Result.success(sysAreaService.getListByPid(pid));
    }

    /**
     * 获取地区树
     *
     * @param level
     * @return
     */
    @GetMapping("/tree/{level}")
    public Result<JSONArray> getAreaTree(@PathVariable("level") Integer level) {
        return Result.success(sysAreaService.getAreaTree(level));
    }

    public Result<JSONArray> getAreaByAreaCode(Integer id) {
        return Result.success(sysAreaService.getAreaByAreaCode(id));
    }

}
