package com.niu.core.controller.adminapi.pay;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.pay.IPayTransferService;
import com.niu.core.service.admin.pay.param.PayTransferParam;
import com.niu.core.service.admin.pay.param.PayTransferSearchParam;
import com.niu.core.service.admin.pay.param.SetSceneIdParam;
import com.niu.core.service.admin.pay.vo.PayTransferInfoVo;
import com.niu.core.service.admin.pay.vo.PayTransferListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.core.pay.param.SetTradeSceneParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 转账控制器
 */

@RestController
@RequestMapping("adminapi/pay")
@SaCheckLogin
public class PayTransferController {

    @Resource
    IPayTransferService payTransferService;

    /**
     * 获取转账场景
     * @return
     */
    @GetMapping("/transfer_scene")
    public Result<?> transferScene() {
        return Result.success(payTransferService.getWechatTransferScene());
    }

    /**
     * 设置场景id
     * @return
     */
    @PostMapping("/transfer_scene/set_scene_id/{scene}")
    public Result<?> setSceneId(@PathVariable("scene") String scene, @Validated @RequestBody SetSceneIdParam param) {
        param.setScene(scene);
        payTransferService.setSceneId(param);
        return Result.success();
    }

    /**
     * 设置业务场景配置
     * @return
     */
    @PostMapping("/transfer_scene/set_trade_scene/{type}")
    public Result<?> setTradeScene(@PathVariable("type") String type, @RequestBody SetTradeSceneParam param) {
        payTransferService.setTradeScene(param);
        return Result.success();
    }
}
