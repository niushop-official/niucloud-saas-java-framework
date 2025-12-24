package com.niu.core.controller.adminapi.channel;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.enums.HttpEnum;
import com.niu.core.enums.channel.AppDict;
import com.niu.core.service.admin.channel.IAdminAppService;
import com.niu.core.service.admin.channel.param.AppVersionAddParam;
import com.niu.core.service.admin.channel.param.AppVersionPageParam;
import com.niu.core.service.admin.channel.vo.AppVersionInfoVo;
import com.niu.core.service.admin.channel.vo.AppVersionListVo;
import com.niu.core.service.core.channel.ICoreAppCloudService;
import com.niu.core.service.core.channel.param.GenerateSignCertParam;
import com.niu.core.service.core.channel.param.SetAppParam;
import com.niu.core.service.core.channel.vo.AppCompileLogVo;
import com.niu.core.service.core.channel.vo.AppConfigVo;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jdk.jfr.Description;
import java.util.Map;

/**
 * APP管理控制器
 */
@RestController
@RequestMapping("/adminapi/channel/app")
@Description("APP管理")
public class AppController {

    @Resource
    private IAdminAppService adminAppService;

    @Resource
    private ICoreAppCloudService coreAppCloudService;

    /**
     * 获取APP配置
     * @return Result<AppConfigVo>
     */
    @GetMapping("/config")
    @Description("获取APP配置")
    public Result<AppConfigVo> getAppConfig() {
        AppConfigVo config = adminAppService.getAppConfig();
        return Result.success(config);
    }

    /**
     * 设置APP配置
     * @param param APP配置参数
     * @return Result<Boolean>
     */
    @PutMapping("/config")
    @Description("设置APP配置")
    public Result setAppConfig(@Valid @RequestBody SetAppParam param) {
        adminAppService.setAppConfig(param);
        return Result.success();
    }

    /**
     * 获取版本列表
     * @param param 分页查询参数
     * @return Result<PageResult<AppVersionListVo>>
     */
    @GetMapping("/version")
    @Description("获取版本列表")
    public Result<PageResult<AppVersionListVo>> getVersionList(PageParam pageParam, AppVersionPageParam param) {
        PageResult<AppVersionListVo> list = adminAppService.getVersionPage(pageParam, param);
        return Result.success(list);
    }

    /**
     * 获取版本详情
     * @param id 版本ID
     * @return Result<AppVersionInfoVo>
     */
    @GetMapping("/version/{id}")
    @Description("获取版本详情")
    public Result<AppVersionInfoVo> getVersionInfo(@PathVariable("id") Integer id) {
        AppVersionInfoVo info = adminAppService.getVersionInfo(id);
        return Result.success(info);
    }

    /**
     * 添加版本
     * @param param 添加版本参数
     * @return Result<Boolean>
     */
    @PostMapping("/version")
    @Description("添加版本")
    public Result<Boolean> addVersion(@Valid @RequestBody AppVersionAddParam param) {
        boolean result = adminAppService.addVersion(param);
        return Result.success(result);
    }

    /**
     * 编辑版本
     * @param param 编辑版本参数
     * @return Result<Boolean>
     */
    @PutMapping("/version/{id}")
    @Description("编辑版本")
    public Result<Boolean> editVersion(@PathVariable("id") Integer id, @Valid @RequestBody AppVersionAddParam param) {
        boolean result = adminAppService.editVersion(id, param);
        return Result.success(result);
    }

    /**
     * 删除版本
     * @param id 版本ID
     * @return Result<Boolean>
     */
    @DeleteMapping("/version/{id}")
    @Description("删除版本")
    public Result delVersion(@PathVariable("id") Integer id) {
        adminAppService.delVersion(id);
        return Result.success();
    }

    /**
     * 获取APP平台信息
     * @return Result<Map<String, Object>>
     */
    @GetMapping("/platfrom")
    @Description("获取APP平台信息")
    public Result<Map<String, Object>> getAppPlatform() {
        return Result.success(AppDict.getPlatformMap());
    }

    /**
     * 获取构建日志
     * @param key 版本key
     * @return Result<String>
     */
    @GetMapping("/build/log/{key}")
    @Description("获取构建日志")
    public Result<AppCompileLogVo> getBuildLog(@PathVariable("key") String key) {
        return Result.success(adminAppService.getBuildLog(key));
    }

    /**
     * 发布版本
     * @param id 版本ID
     * @return Result<Boolean>
     */
    @PutMapping("/version/{id}/release")
    @Description("发布版本")
    public Result<Boolean> release(@PathVariable("id") Integer id) {
        boolean result = adminAppService.release(id);
        return Result.success(result);
    }

    /**
     * 生成签名证书
     * @param param 证书生成参数
     * @return Result<Map<String, Object>>
     */
    @PostMapping("/generate_sing_cert")
    @Description("生成签名证书")
    public Result<String> generateSignCert(@RequestBody GenerateSignCertParam param) {
        return Result.success(HttpEnum.SUCCESS.getCode(), HttpEnum.SUCCESS.getMsg(), coreAppCloudService.generateSignCert(param));
    }
}