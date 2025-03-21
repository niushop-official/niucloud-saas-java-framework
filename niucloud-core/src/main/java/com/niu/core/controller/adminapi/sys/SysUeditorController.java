package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysConfigService;
import com.niu.core.service.admin.sys.vo.SysCopyRightVo;
import com.niu.core.service.admin.sys.vo.SysUeditorConfigVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("adminapi/sys/")
@SaCheckLogin
public class SysUeditorController {

    /**
     * 获取百度编译器配置
     * @return
     */
    @GetMapping("/ueditor")
    public Result<SysUeditorConfigVo> getConfig(){
        SysUeditorConfigVo sysUeditorConfigVo=new SysUeditorConfigVo();
        return Result.success(sysUeditorConfigVo);
    }

    /**
     * 百度编辑器文件上传
     * @return
     */
    @PostMapping("/ueditor")
    public Result<Object> upload(){

        return Result.success();
    }
}
