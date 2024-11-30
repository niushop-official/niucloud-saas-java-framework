package com.niu.core.controller.adminapi.sys;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISysPrinterService;
import com.niu.core.service.admin.sys.param.SysPrinterModifyStatusParam;
import com.niu.core.service.admin.sys.param.SysPrinterParam;
import com.niu.core.service.admin.sys.param.SysPrinterSearchParam;
import com.niu.core.service.core.sys.param.SysPrinterPrintTicketParam;
import com.niu.core.service.core.sys.vo.SysPrinterPrintTicketVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 小票打印机控制器
 */

@RestController
@RequestMapping("adminapi/sys/printer")
public class SysPrinterController {

    @Resource
    ISysPrinterService sysPrinterService;

    /**
     * 小票打印机分页列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < SysPrinterListVo>>
     */
    @GetMapping
    public Result<?> page(@Validated PageParam pageParam, @Validated SysPrinterSearchParam searchParam) {
        return Result.success(sysPrinterService.page(pageParam, searchParam));
    }

    @GetMapping("/list")
    public Result<?> lists(@Validated SysPrinterSearchParam searchParam) {
        return Result.success(sysPrinterService.getList(searchParam));
    }

    /**
     * 小票打印机详情
     *
     * @param id 主键ID
     * @return Result<SysPrinterInfoVo>
     */
    @GetMapping("/{id}")
    public Result<?> info(@PathVariable Integer id) {
        return Result.success(sysPrinterService.info(id));
    }

    /**
     * 小票打印机添加
     *
     * @param addParam 添加参数
     * @return Result<?>
     */
    @PostMapping
    public Result<?> add(@Validated @RequestBody SysPrinterParam addParam) {
        sysPrinterService.add(addParam);
        return Result.success();
    }

    /**
     * 小票打印机编辑
     *
     * @param editParam 编辑参数
     * @return Result<?>
     */
    @PutMapping("/{id}")
    public Result<?> edit(@PathVariable Integer id, @Validated @RequestBody SysPrinterParam editParam) {
        sysPrinterService.edit(id, editParam);
        return Result.success();
    }

    @PutMapping("/status")
    public Result<?> modifyStatus(@Validated @RequestBody SysPrinterModifyStatusParam editParam) {
        sysPrinterService.modifyStatus(editParam);
        return Result.success();
    }

    /**
     * 小票打印机删除
     *
     * @param id 参数
     * @return Result<?>
     */
    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Integer id) {
        sysPrinterService.del(id);
        return Result.success();
    }

    @GetMapping("/type")
    public Result<?> getType() {
        return Result.success(sysPrinterService.getType());
    }

    @GetMapping("/brand")
    public Result<?> getBrand() {
        return Result.success(sysPrinterService.getBrand());
    }

    @PutMapping("/refreshtoken/{id}")
    public Result<?> refreshToken(@PathVariable Integer id) {
        return Result.success(sysPrinterService.refreshToken(id));
    }

    @PutMapping("/testprint/{id}")
    public Result<?> testPrint(@PathVariable Integer id) {
        sysPrinterService.testPrint(id);
        return Result.success();
    }

    @PostMapping("/printticket")
    public Result<?> printTicket(@RequestBody SysPrinterPrintTicketParam param) {
        SysPrinterPrintTicketVo vo = sysPrinterService.printTicket(param);
        if (vo.getCode() == 0) {
            return Result.success();
        }

        return Result.fail(vo.getMessage());
    }

}
