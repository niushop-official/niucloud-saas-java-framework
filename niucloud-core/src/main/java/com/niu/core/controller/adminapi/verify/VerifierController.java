package com.niu.core.controller.adminapi.verify;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.sys.VerifyTypeEnum;
import com.niu.core.service.admin.verify.param.VerifierParam;
import com.niu.core.service.admin.verify.param.VerifierSearchParam;
import com.niu.core.service.admin.verify.IVerifierService;
import com.niu.core.service.admin.verify.vo.VerifierInfoVo;
import com.niu.core.service.admin.verify.vo.VerifierListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 核销员控制器
 */

@RestController
@RequestMapping("adminapi/verify/verifier")
@SaCheckLogin
public class VerifierController {

    @Resource
    IVerifierService verifierService;

    /**
     * 核销员列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<VerifierListVo>>
     */
    @GetMapping("")
    public Result<PageResult<VerifierListVo>> list(@Validated PageParam pageParam, @Validated VerifierSearchParam searchParam) {
        PageResult<VerifierListVo> list = verifierService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取核销员列表
     * @return
     */
    @GetMapping("/select")
    public Result<List<VerifierListVo>> all() {
        return Result.success(verifierService.all());
    }

    /**
     * 核销员添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody VerifierParam addParam) {
        verifierService.add(addParam);
        return Result.success();
    }

    /**
     * 核销员删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable Integer id) {
        verifierService.del(id);
        return Result.success();
    }

    /**
     * 获取核销类型
     * @return Result<JSONObject>
     */
    @GetMapping("/type")
    public Result<JSONObject> getVerifyType() {
        return Result.success(VerifyTypeEnum.getType());
    }
}
