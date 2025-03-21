package com.niu.core.controller.adminapi.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.IMemberLevelService;
import com.niu.core.service.admin.member.param.MemberLevelParam;
import com.niu.core.service.admin.member.param.MemberLevelSearchParam;
import com.niu.core.service.admin.member.vo.MemberLevelAllListVo;
import com.niu.core.service.admin.member.vo.MemberLevelInfoVo;
import com.niu.core.service.admin.member.vo.MemberLevelListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 会员等级控制器
 */

@RestController
@RequestMapping("adminapi/member/level")
@SaCheckLogin
public class MemberLevelController {

    @Resource
    IMemberLevelService memberLevelService;

    /**
     * 会员等级列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MemberLevelListVo>>
     */
    @GetMapping("")
    public Result<PageResult<MemberLevelListVo>> pages(@Validated PageParam pageParam, @Validated MemberLevelSearchParam searchParam) {
        PageResult<MemberLevelListVo> list = memberLevelService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 会员等级详情
     * @param id 主键ID
     * @return Result<MemberLevelInfoVo>
     */
    @GetMapping("/{id}")
    public Result<MemberLevelInfoVo> info(@PathVariable("id") Integer id) {
        MemberLevelInfoVo info = memberLevelService.info(id);
        return Result.success(info);
    }

    /**
     * 会员等级添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody MemberLevelParam addParam) {
        memberLevelService.add(addParam);
        return Result.success();
    }

    /**
     * 会员等级编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody MemberLevelParam editParam) {
        memberLevelService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 会员等级删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        memberLevelService.del(id);
        return Result.success();
    }

    /**
     * 全部会员等级
     * @return
     */
    @GetMapping("/all")
    public Result<List<MemberLevelAllListVo>> all() {
        List<MemberLevelAllListVo> list = memberLevelService.all();
        return Result.success(list);
    }
}
