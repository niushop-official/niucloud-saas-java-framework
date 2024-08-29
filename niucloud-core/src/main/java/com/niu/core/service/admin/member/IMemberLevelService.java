package com.niu.core.service.admin.member;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.param.MemberLevelParam;
import com.niu.core.service.admin.member.param.MemberLevelSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.member.vo.MemberLevelAllListVo;
import com.niu.core.service.admin.member.vo.MemberLevelInfoVo;
import com.niu.core.service.admin.member.vo.MemberLevelListVo;

import java.util.List;

/**
 * 会员等级服务接口
 */
public interface IMemberLevelService {


    /**
     * 会员等级列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberLevelListVo>
     */
    PageResult<MemberLevelListVo> list(PageParam pageParam, MemberLevelSearchParam searchParam);

    /**
     * 会员等级详情
     * @param id 主键ID
     * @return MemberLevelInfoVo
     */
    MemberLevelInfoVo info(Integer id);

    /**
     * 会员等级添加
     * @param addParam 添加参数
     */
    void add(MemberLevelParam addParam);

    /**
     * 会员等级编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MemberLevelParam editParam);

    /**
     * 会员等级删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 全部会员等级
     * @return
     */
    List<MemberLevelAllListVo> all();
}
