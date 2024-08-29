package com.niu.core.service.admin.member;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.param.MemberAddressParam;
import com.niu.core.service.admin.member.param.MemberAddressSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.member.vo.MemberAddressInfoVo;
import com.niu.core.service.admin.member.vo.MemberAddressListVo;

/**
 * 会员收货地址服务接口
 */
public interface IMemberAddressService {


    /**
     * 会员收货地址列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberAddressListVo>
     */
    PageResult<MemberAddressListVo> list(PageParam pageParam, MemberAddressSearchParam searchParam);

    /**
     * 会员收货地址详情
     * @param id 主键ID
     * @return MemberAddressInfoVo
     */
    MemberAddressInfoVo info(Integer id);

    /**
     * 会员收货地址添加
     * @param addParam 添加参数
     */
    void add(MemberAddressParam addParam);

    /**
     * 会员收货地址编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MemberAddressParam editParam);

    /**
     * 会员收货地址删除
     * @param id 主键ID
     */
    void del(Integer id);

}
