package com.niu.core.controller.adminapi.diy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.diy.IDiyConfigService;
import com.niu.core.service.admin.diy.param.SetBottomConfigParam;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 自定义页面配置
 */
@RestController
@RequestMapping("/adminapi/diy")
@SaCheckLogin
public class ConfigController {

    @Resource
    IDiyConfigService diyConfigService;

    /**
     * 底部导航配置列表
     *
     * @return
     */
    @GetMapping("/bottom")
    public Result<List<BottomConfigVo>> getBottomList() {
        return Result.success(diyConfigService.getBottomList());
    }

    /**
     * 底部导航配置
     *
     * @return
     */
    @GetMapping("/bottom/config")
    public Result<BottomConfigVo> getBottomConfig(@RequestParam("key") String key) {
        if(key == null|| key.equals("")) key = "app";
        return Result.success(diyConfigService.getBottomConfig(key));
    }

    /**
     * 设置底部导航
     *
     * @return
     */
    @PostMapping("/bottom")
    public Result setBottomConfig(@RequestBody SetBottomConfigParam param){
        diyConfigService.setBottomConfig(param);
        return Result.success();
    }
}
