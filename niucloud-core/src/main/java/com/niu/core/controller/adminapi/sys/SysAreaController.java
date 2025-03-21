package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.service.admin.sys.ISysAreaService;
import com.niu.core.service.admin.sys.vo.SysAreaByCodeVo;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    /**
     *
     * @param code
     * @return
     */
    @GetMapping("/code/{code}")
    public Result<Map<String, SysAreaByCodeVo>> getAreaByAreaCode(@PathVariable("code") Integer code) {
        return Result.success(sysAreaService.getAreaByAreaCode(code));
    }

    /**
     * 逆地址解析
     *
     * @param location
     * @return
     */
    @GetMapping("/contrary")
    public Result contraryAddress(@RequestParam("location") String location) {
        return Result.success(sysAreaService.getAddressInfo(location));
    }

    /**
     * 地址解析
     * @param address
     * @return
     */
    @GetMapping("/get_info")
    public Result addressInfo(@RequestParam("address") String address) {
        return Result.success(sysAreaService.getAddress(address));
    }
}
