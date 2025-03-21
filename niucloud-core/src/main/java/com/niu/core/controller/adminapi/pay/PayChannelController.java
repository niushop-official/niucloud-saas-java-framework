package com.niu.core.controller.adminapi.pay;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.service.admin.pay.IPayChannelService;
import com.niu.core.service.admin.pay.param.PayChannelAllSetParam;
import com.niu.core.service.admin.pay.param.PayChannelParam;
import com.niu.core.service.admin.pay.param.PayChannelSearchParam;
import com.niu.core.service.admin.pay.vo.PayChannelInfoVo;
import com.niu.core.service.admin.pay.vo.PayChannelListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.pay.vo.PayChanneltemVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 支付渠道配置控制器
 */

@RestController
@RequestMapping("adminapi/pay")
@SaCheckLogin
public class PayChannelController {

    @Resource
    IPayChannelService payChannelService;

    /**
     * 支付渠道配置列表
     * @return Result<PageResult<PayChannelListVo>>
     */
    @GetMapping("/channel/lists")
    public Result<Map<String, PayChanneltemVo>> list() {
        return Result.success(payChannelService.list());
    }

    /**
     * 获取全部支付方式
     * @return
     */
    @GetMapping("/type/all")
    public Result<JSONObject> getAllPayType() {
        return Result.success(PayTypeEnum.getType());
    }

    /**
     * 多渠道设置
     * @return
     */
    @PostMapping("/channel/set/all")
    public Result setAll(@Validated @RequestBody PayChannelAllSetParam param) {
        payChannelService.setAll(param);
        return Result.success();
    }

    /**
     * 多渠道设置
     * @return
     */
    @PostMapping("/channel/set/{channel}/{type}")
    public Result setAll(@PathVariable("channel") String channel, @PathVariable("type") String type, @RequestBody JSONObject param) {
        payChannelService.set(channel, type, param);
        return Result.success();
    }

    /**
     * 通过渠道获取支付配置
     * @param channel
     * @return
     */
    @GetMapping("/channel/lists/{channel}")
    public Result<List<PayChannelListVo>> getListByChannel(@PathVariable("channel") String channel){
        return Result.success(payChannelService.getListByChannel(channel));
    }

    /**
     * 转账设置
     * @param param
     * @return
     */
    @PostMapping("/channel/set/transfer")
    public Result setTransfer(@RequestBody JSONObject param) {
        payChannelService.setTransfer(param);
        return Result.success();
    }}
