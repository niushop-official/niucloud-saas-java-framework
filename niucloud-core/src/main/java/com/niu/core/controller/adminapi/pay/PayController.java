package com.niu.core.controller.adminapi.pay;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.pay.IPayService;
import com.niu.core.service.admin.pay.param.GetFriendspayInfoByTradeParam;
import com.niu.core.service.admin.pay.param.PayParam;
import com.niu.core.service.admin.pay.param.PaySearchParam;
import com.niu.core.service.admin.pay.vo.PayInfoVo;
import com.niu.core.service.admin.pay.vo.PayListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 支付记录控制器
 */

@RestController
@RequestMapping("adminapi/pay")
@SaCheckLogin
public class PayController {

    @Resource
    IPayService payService;

    /**
     * 支付记录列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < PayListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<PayListVo>> list(@Validated PageParam pageParam,
                                              @Validated PaySearchParam searchParam) {
        PageResult<PayListVo> list = payService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 支付记录详情
     *
     * @param id 主键ID
     * @return Result<PayInfoVo>
     */
    @GetMapping("/info")
    public Result<PayInfoVo> info(@Validated @RequestParam("id") Integer id) {
        PayInfoVo info = payService.info(id);
        return Result.success(info);
    }

    /**
     * 支付记录添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody PayParam addParam) {
        payService.add(addParam);
        return Result.success();
    }

    /**
     * 支付记录编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody PayParam editParam) {
        payService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 支付记录删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        payService.del(id);
        return Result.success();
    }

    @GetMapping("/friendspay/info/{trade_type}/{trade_id}/{channel}")
    public Result<?> getFriendspayInfoByTrade(@PathVariable("trade_type") String tradeType, @PathVariable("trade_id") Integer tradeId, @PathVariable("channel") String channel) {
        GetFriendspayInfoByTradeParam param = new GetFriendspayInfoByTradeParam();
        param.setTradeType(tradeType);
        param.setTradeId(tradeId);
        param.setChannel(channel);
        return Result.success(payService.getFriendspayInfoByTrade(param));
    }

    @GetMapping("/type/list")
    public Result<?> payTypeList() {
        return Result.success(payService.getPayTypeList());
    }

    @PostMapping
    public Result<?> pay(@RequestBody com.niu.core.common.loader.pay.param.PayParam param) {
        return Result.success(payService.pay(param));
    }

}
