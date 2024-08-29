package com.niu.core.service.admin.member;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.param.MemberCashOutAccountParam;
import com.niu.core.service.admin.member.param.MemberCashOutAccountSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.member.vo.MemberCashOutAccountInfoVo;
import com.niu.core.service.admin.member.vo.MemberCashOutAccountListVo;

/**
 * 会员提现账户服务接口
 */
public interface IMemberCashOutAccountService {


    /**
     * 会员提现账户列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberCashOutAccountListVo>
     */
    PageResult<MemberCashOutAccountListVo> list(PageParam pageParam, MemberCashOutAccountSearchParam searchParam);

    /**
     * 会员提现账户详情
     * @param id 主键ID
     * @return MemberCashOutAccountInfoVo
     */
    MemberCashOutAccountInfoVo info(Integer id);

    /**
     * 会员提现账户添加
     * @param addParam 添加参数
     */
    void add(MemberCashOutAccountParam addParam);

    /**
     * 会员提现账户编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MemberCashOutAccountParam editParam);

    /**
     * 会员提现账户删除
     * @param id 主键ID
     */
    void del(Integer id);

}
