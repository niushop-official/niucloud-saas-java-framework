package com.niu.core.controller.adminapi.pay;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.pay.IPayTransferService;
import com.niu.core.service.admin.pay.param.PayTransferParam;
import com.niu.core.service.admin.pay.param.PayTransferSearchParam;
import com.niu.core.service.admin.pay.vo.PayTransferInfoVo;
import com.niu.core.service.admin.pay.vo.PayTransferListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 转账控制器
 */

@RestController
@RequestMapping("adminapi/transfer")
@SaCheckLogin
public class PayTransferController {

    @Resource
    IPayTransferService payTransferService;

    /**
     * 转账列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<PayTransferListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<PayTransferListVo>> list(@Validated PageParam pageParam,
                                                     @Validated PayTransferSearchParam searchParam) {
        PageResult<PayTransferListVo> list = payTransferService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 转账详情
     * @param id 主键ID
     * @return Result<PayTransferInfoVo>
     */
    @GetMapping("/info")
    public Result<PayTransferInfoVo> info(@Validated @RequestParam("id") Integer id) {
        PayTransferInfoVo info = payTransferService.info(id);
        return Result.success(info);
    }

    /**
     * 转账添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody PayTransferParam addParam) {
        payTransferService.add(addParam);
        return Result.success();
    }

    /**
     * 转账编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody PayTransferParam editParam) {
        payTransferService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 转账删除
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        payTransferService.del(id);
        return Result.success();
    }

}
