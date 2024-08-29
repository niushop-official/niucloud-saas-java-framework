package com.niu.core.controller.api.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.member.IMemberAddressService;
import com.niu.core.service.api.member.param.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 会员地址
 */
@RestController
@RequestMapping("/api/member")
@Slf4j
@SaCheckLogin
public class MemberAddressController {

    @Autowired
    private IMemberAddressService memberAddressService;

    /**
     * 会员收货地址列表
     *
     * @return
     */
    @GetMapping("/address")
    public Result<?> addressList() {
        MemberAddressGetListParam memberAddressGetListParam = new MemberAddressGetListParam();
        return Result.success(memberAddressService.getList(memberAddressGetListParam));
    }

    /**
     * 会员收货地址详情
     *
     * @return
     */
    @GetMapping("/address/{id}")
    public Result<?> addressInfo(@PathVariable("id") Integer id) {
        MemberAddressGetInfoParam memberAddressGetInfoParam = new MemberAddressGetInfoParam();
        memberAddressGetInfoParam.setId(id);
        return Result.success(memberAddressService.getInfo(memberAddressGetInfoParam));
    }

    /**
     * 添加会员收货地址
     *
     * @return
     */
    @PostMapping("/address")
    public Result<?> addAddress(@RequestBody MemberAddressAddParam param) {
        return Result.success(memberAddressService.add(param));
    }

    /**
     * 编辑会员收货地址
     *
     * @return
     */
    @PutMapping("/address/{id}")
    public Result<?> editAddress(@PathVariable("id") Integer id, @RequestBody MemberAddressEditParam param) {
        memberAddressService.edit(id, param);
        return Result.success();
    }

    /**
     * 删除会员收货地址
     *
     * @return
     */
    @DeleteMapping("/address/{id}")
    public Result<?> deleteAddress(@PathVariable("id") Integer id) {
        MemberAddressDeleteParam param = new MemberAddressDeleteParam();
        param.setId(id);
        return Result.success(memberAddressService.delete(param));
    }

}
