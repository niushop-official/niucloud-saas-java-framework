package com.niu.core.controller.adminapi.notice;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.notice.INuiSmsService;
import com.niu.core.service.admin.notice.param.*;
import com.niu.core.service.api.login.param.SendMobileCodeParam;
import jakarta.annotation.Resource;
import jdk.jfr.Description;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Description("牛云短信管理")
@RestController
@RequestMapping("adminapi/notice/niusms")
@SaCheckLogin
public class NiuSmsController {

    @Resource
    private INuiSmsService nuiSmsService;

    @Description("获取短信配置")
    @GetMapping("/config")
    public Result<Map<String, Object>> getConfig() {
        return Result.success(nuiSmsService.getConfig());
    }

    /**
     * 获取创建签名初始化的配置信息
     *
     * @return
     */
    @Description("获取创建签名初始化配置")
    @GetMapping("/sign/report/config")
    public Result<Map<String, Object>> signCreateConfig() {
        return Result.success(nuiSmsService.signCreateConfig());
    }

    /**
     * 获取图形验证码
     */
    @Description("获取图形验证码")
    @GetMapping("/captcha")
    public Result<JSONObject> captcha() {
        return Result.success(nuiSmsService.captcha());
    }

    /**
     * 手机号发送验证码
     *
     * @return
     */
    @Description("发送手机验证码")
    @PostMapping("/send")
    public Result<?> sendMobileCode(@Validated @RequestBody SendMobileCodeParam param) {
        return Result.success(nuiSmsService.sendMobileCode(param));
    }

    /**
     * 注册牛之云子账号
     *
     * @return
     */
    @Description("注册牛之云子账号")
    @PostMapping("/account/register")
    public Result<?> registerAccount(@RequestBody RegisterAccountParam param) {
        return Result.success(nuiSmsService.registerAccount(param));
    }

    /**
     * 登录牛之云子账号
     *
     * @return
     */
    @Description("登录牛之云子账号")
    @PostMapping("/account/login")
    public Result<?> loginAccount(@RequestBody RegisterAccountParam param) {
        return Result.success(nuiSmsService.loginAccount(param));
    }

    /**
     * 重置密码
     *
     * @param param
     * @return
     */
    @Description("重置密码")
    @PostMapping("/account/reset/password/{username}")
    public Result<?> resetPassword(@PathVariable("username") String username, @RequestBody RegisterAccountParam param) {
        param.setUsername(username);
        return Result.success(nuiSmsService.resetPassword(param));
    }

    /**
     * 获取子账号信息
     *
     * @param username
     * @return
     */
    @Description("获取子账号信息")
    @GetMapping("/account/info/{username}")
    public Result<?> accountInfo(@PathVariable("username") String username) {
        return Result.success(nuiSmsService.accountInfo(username));
    }


    /**
     * 创建模版需要的配置
     *
     * @param
     * @return
     */
    @Description("创建模板需要的配置")
    @GetMapping("/template/report/config")
    public Result<?> templateCreateConfig() {
        return Result.success(nuiSmsService.templateCreateConfig());
    }

    /**
     * 模版列表
     *
     * @param smsType
     * @param username
     * @return
     */
    @Description("模板列表")
    @GetMapping("/template/list/{smsType}/{username}")
    public Result<?> templateList(@PathVariable("smsType") String smsType, @PathVariable("username") String username) {
        return Result.success(nuiSmsService.getTemplateList(smsType, username));
    }

    /**
     * 充值订单列表
     *
     * @param username
     * @param pageParam
     * @param param
     * @return
     */
    @Description("充值订单列表")
    @GetMapping("/order/list/{username}")
    public Result<?> orderList(@PathVariable("username") String username, @Validated PageParam pageParam, OrderListParam param) {
        return Result.success(nuiSmsService.orderList(pageParam, username, param));
    }

    /**
     * 获取短信发送列表
     *
     * @param username
     * @param pageParam
     * @param param
     * @return
     */
    @Description("获取短信发送列表")
    @GetMapping("/account/send_list/{username}")
    public Result<?> accountSendList(@PathVariable("username") String username, @Validated PageParam pageParam, SendListParam param) {
        return Result.success(nuiSmsService.accountSendList(pageParam, username, param));
    }

    /**
     * 启用牛云短信
     *
     * @param param
     * @return
     */
    @Description("启用牛云短信")
    @PutMapping("/enable")
    public Result<?> enable(@RequestBody EnableParam param) {
        nuiSmsService.enable(param.getIsEnable());
        return Result.success();
    }

    /**
     * 修改子账户信息
     */
    @Description("修改子账户信息")
    @PostMapping("/account/edit/{username}")
    public Result<?> editAccount(@PathVariable("username") String username, @RequestBody EditAccountParam param) {
        return Result.success(nuiSmsService.editAccount(username, param));
    }

    /**
     * 获取签名列表
     *
     * @param username
     * @param pageParam
     * @return
     */
    @Description("获取签名列表")
    @GetMapping("/sign/list/{username}")
    public Result<?> signList(@PathVariable("username") String username, @Validated PageParam pageParam) {
        return Result.success(nuiSmsService.getSignList(pageParam, username));
    }

    /**
     * 删除签名
     *
     * @param username
     * @return
     */
    @Description("删除签名")
    @PostMapping("/sign/delete/{username}")
    public Result<?> signDelete(@PathVariable("username") String username, @RequestBody SignDeleteParam param) {
        return Result.success(nuiSmsService.signDelete(username, param));
    }

    /**
     * 创建签名
     *
     * @param username
     * @param param
     * @return
     */
    @Description("创建签名")
    @PostMapping("/sign/report/{username}")
    public Result<?> signCreate(@PathVariable("username") String username, @RequestBody RegisterAccountParam param) {
        nuiSmsService.signCreate(username, param);
        return Result.success();
    }

    /**
     * 获取短信套餐列表
     */
    @Description("获取短信套餐列表")
    @GetMapping("packages")
    public Result<?> getSmsPackageList(SmsPackageParam param) {
        return Result.success(nuiSmsService.getSmsPackageList(param));
    }

    /**
     * 订单计算
     * @param username
     * @param param
     * @return
     */
    @Description("订单计算")
    @PostMapping("/order/calculate/{username}")
    public Result<?> orderCalculate(@PathVariable("username") String username, @RequestBody OrderCalculateParam param) {
        return Result.success(nuiSmsService.orderCalculate(username, param));
    }

    /**
     * 创建订单
     * @param username
     * @param param
     * @return
     */
    @Description("创建订单")
    @PostMapping("/order/create/{username}")
    public Result<?> createOrder(@PathVariable("username") String username, @RequestBody OrderCalculateParam param) {
        return Result.success(nuiSmsService.createOrder(username, param));
    }

    /**
     * 获取订单支付信息
     * @param username
     * @param outTradeNo
     * @return
     */
    @Description("获取订单支付信息")
    @GetMapping("/order/pay/{username}")
    public Result<?> getPayInfo(@PathVariable("username") String username, @RequestParam("out_trade_no")String outTradeNo) {
        return Result.success(nuiSmsService.getPayInfo(username, outTradeNo));
    }

    /**
     * 订单详情
     * @param username
     * @param outTradeNo
     * @return
     */
    @Description("订单详情")
    @GetMapping("/order/info/{username}")
    public Result<?> orderInfo(@PathVariable("username") String username, @RequestParam("out_trade_no")String outTradeNo) {
        return Result.success(nuiSmsService.getOrderInfo(username, outTradeNo));
    }

    /**
     * 订单状态
     * @param username
     * @param outTradeNo
     * @return
     */
    @Description("订单状态")
    @GetMapping("/order/status/{username}")
    public Result<?> orderStatus(@PathVariable("username") String username, @RequestParam("out_trade_no")String outTradeNo) {
        return Result.success(nuiSmsService.getOrderStatus(username, outTradeNo));
    }

    /**
     * 模版同步
     * @param smsType
     * @param username
     * @return
     */
    @Description("模版同步")
    @GetMapping("/template/sync/{smsType}/{username}")
    public Result<?> templateSync(@PathVariable("smsType") String smsType, @PathVariable("username") String username) {
        nuiSmsService.templateSync(username, smsType);
        return Result.success();
    }

    /**
     * 创建模板
     *
     */
    @Description("创建模板")
    @PostMapping("/template/report/{smsType}/{username}")
    public Result<?> templateCreate(@PathVariable("smsType") String smsType, @PathVariable("username") String username, @RequestBody TemplateCreateParam param) {
        return Result.success(nuiSmsService.templateCreate(username, smsType, param));
    }

    /**
     * 删除模板
     *
     */
    @Description("删除模板")
    @DeleteMapping("/template/{username}/{templateId}")
    public Result<?> templateDelete(@PathVariable("username") String username, @PathVariable("templateId") String templateId) {
        nuiSmsService.templateDelete(username, templateId);
        return Result.success();
    }

    /**
     * 模板信息
     */
    @Description("模板信息")
    @GetMapping("/template/info/{smsType}/{username}")
    public Result<?> templateInfo(@PathVariable("smsType") String smsType, @PathVariable("username") String username, @RequestParam("template_key") String templateKey) {
        return Result.success(nuiSmsService.templateInfo(username, smsType, templateKey));
    }
}
