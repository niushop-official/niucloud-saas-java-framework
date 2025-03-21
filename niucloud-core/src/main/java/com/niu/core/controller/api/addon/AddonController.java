package com.niu.core.controller.api.addon;

import com.niu.core.common.domain.Result;
import com.niu.core.service.api.sys.ITaskService;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/addon")
public class AddonController {

    @Resource
    ICoreAddonService coreAddonService;

    @GetMapping("/list/install")
    public Result<Map<String, InstallAddonListVo>> getInstallList() {
        return Result.success(coreAddonService.getInstallAddonList());
    }


}
