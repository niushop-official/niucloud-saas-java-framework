package com.niu.core.controller.adminapi.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.member.IMemberAddressService;
import com.niu.core.service.admin.member.param.MemberAddressParam;
import com.niu.core.service.admin.member.param.MemberAddressSearchParam;
import com.niu.core.service.admin.member.vo.MemberAddressInfoVo;
import com.niu.core.service.admin.member.vo.MemberAddressListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 会员收货地址控制器
 */

@RestController
@RequestMapping("adminapi/member/address")
@SaCheckLogin
public class MemberAddressController {

    @Resource
    IMemberAddressService memberAddressService;

    /**
     * 会员收货地址列表
     *
     * @param searchParam 搜索条件
     * @return Result<PageResult < MemberAddressListVo>>
     */
    @GetMapping("")
    public Result<List<MemberAddressListVo>> list(MemberAddressSearchParam searchParam) {
        return Result.success(memberAddressService.list(searchParam));
    }

    /**
     * 会员收货地址详情
     *
     * @param id 主键ID
     * @return Result<MemberAddressInfoVo>
     */
    @GetMapping("/{id}")
    public Result<MemberAddressInfoVo> info(@PathVariable("id") Integer id) {
        MemberAddressInfoVo info = memberAddressService.info(id);
        return Result.success(info);
    }

    /**
     * 会员收货地址添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody MemberAddressParam addParam) {
        memberAddressService.add(addParam);
        return Result.success();
    }

    /**
     * 会员收货地址编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody MemberAddressParam editParam) {
        memberAddressService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 会员收货地址删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        memberAddressService.del(id);
        return Result.success();
    }

}
