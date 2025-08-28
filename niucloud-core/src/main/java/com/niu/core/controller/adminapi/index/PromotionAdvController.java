package com.niu.core.controller.adminapi.index;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.core.index.ICorePromotionAdvService;
import jakarta.annotation.Resource;
import jdk.jfr.Description;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("adminapi/index")
@SaCheckLogin
@Description("推广广告控制器")
public class PromotionAdvController {

    @Resource
    private ICorePromotionAdvService corePromotionAdvService;

    /**
     * 广告列表
     * @return
     */
    @GetMapping("/adv_list")
    public Result getAdvList() {
        return Result.success(corePromotionAdvService.getIndexAdvList());
    }

}
