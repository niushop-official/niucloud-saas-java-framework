package com.niu.core.controller.adminapi.pay;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.pay.RefundTypeEnum;
import com.niu.core.service.admin.pay.IPayRefundService;
import com.niu.core.service.admin.pay.param.PayRefundSearchParam;
import com.niu.core.service.core.pay.param.PayRefundTransferParam;
import com.niu.core.service.admin.pay.vo.PayRefundInfoVo;
import com.niu.core.service.admin.pay.vo.PayRefundListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 退款记录控制器
 */

@RestController
@RequestMapping("adminapi/pay/refund")
@SaCheckLogin
public class PayRefundController {

    @Resource
    IPayRefundService payRefundService;

    /**
     * 退款记录列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<PayRefundListVo>>
     */
    @GetMapping("")
    public Result<PageResult<PayRefundListVo>> list(@Validated PageParam pageParam, @Validated PayRefundSearchParam searchParam) {
        PageResult<PayRefundListVo> list = payRefundService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 退款记录详情
     *
     * @param refundNo
     * @return Result<PayRefundInfoVo>
     */
    @GetMapping("/{refund_no}")
    public Result<PayRefundInfoVo> info(@Validated @PathVariable("refund_no") String refundNo) {
        PayRefundInfoVo info = payRefundService.info(refundNo);
        return Result.success(info);
    }

    /**
     * 退款方式
     *
     * @return
     */
    @GetMapping("/type")
    public Result<Map<String, String>> getRefundType() {
        return Result.success(RefundTypeEnum.getMap());
    }

    /**
     * 退款转账
     *
     * @return
     */
    @PostMapping("/transfer")
    public Result transfer(@Validated @RequestBody PayRefundTransferParam param) {
        payRefundService.transfer(param);
        return Result.success();
    }
}
