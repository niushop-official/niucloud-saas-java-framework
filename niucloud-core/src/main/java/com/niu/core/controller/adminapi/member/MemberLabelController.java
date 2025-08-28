package com.niu.core.controller.adminapi.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.IMemberLabelService;
import com.niu.core.service.admin.member.param.MemberLabelEditParam;
import com.niu.core.service.admin.member.param.MemberLabelParam;
import com.niu.core.service.admin.member.param.MemberLabelSearchParam;
import com.niu.core.service.admin.member.vo.MemberLabelAllListVo;
import com.niu.core.service.admin.member.vo.MemberLabelInfoVo;
import com.niu.core.service.admin.member.vo.MemberLabelListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;
import jdk.jfr.Description;

/**
 * 会员标签控制器
 */
@Description("会员标签控制器")
@RestController
@RequestMapping("adminapi/member")
@SaCheckLogin
public class MemberLabelController {

    @Resource
    IMemberLabelService memberLabelService;

    /**
     * 会员标签列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MemberLabelListVo>>
     */
    @Description("会员标签列表")
    @GetMapping("/label")
    public Result<PageResult<MemberLabelListVo>> list(@Validated PageParam pageParam, @Validated MemberLabelSearchParam searchParam) {
        PageResult<MemberLabelListVo> list = memberLabelService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 会员标签详情
     * @param id 主键ID
     * @return Result<MemberLabelInfoVo>
     */
    @Description("会员标签详情")
    @GetMapping("/label/{id}")
    public Result<MemberLabelInfoVo> info(@PathVariable("id") Integer id) {
        MemberLabelInfoVo info = memberLabelService.info(id);
        return Result.success(info);
    }

    /**
     * 会员标签添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @Description("会员标签添加")
    @PostMapping("/label")
    public Result<Object> add(@Validated @RequestBody MemberLabelEditParam addParam) {
        memberLabelService.add(addParam);
        return Result.success();
    }

    /**
     * 会员标签编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @Description("会员标签编辑")
    @PutMapping("/label/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody MemberLabelEditParam editParam) {
        memberLabelService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 会员标签删除
     * @param id 参数
     * @return Result<Object>
     */
    @Description("会员标签删除")
    @DeleteMapping("/label/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        memberLabelService.del(id);
        return Result.success();
    }

    /**
     * 全部会员标签
     * @return
     */
    @Description("全部会员标签")
    @GetMapping("/label/all")
    public Result<List<MemberLabelAllListVo>> all() {
        List<MemberLabelAllListVo> list = memberLabelService.all();
        return Result.success(list);
    }
}
