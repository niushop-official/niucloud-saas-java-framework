package com.niu.core.service.admin.member;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.param.MemberLabelEditParam;
import com.niu.core.service.admin.member.param.MemberLabelParam;
import com.niu.core.service.admin.member.param.MemberLabelSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.member.vo.MemberLabelAllListVo;
import com.niu.core.service.admin.member.vo.MemberLabelInfoVo;
import com.niu.core.service.admin.member.vo.MemberLabelListVo;

import java.util.List;

/**
 * 会员标签服务接口
 */
public interface IMemberLabelService {


    /**
     * 会员标签列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberLabelListVo>
     */
    PageResult<MemberLabelListVo> list(PageParam pageParam, MemberLabelSearchParam searchParam);

    /**
     * 会员标签详情
     * @param id 主键ID
     * @return MemberLabelInfoVo
     */
    MemberLabelInfoVo info(Integer id);

    /**
     * 会员标签添加
     * @param addParam 添加参数
     */
    void add(MemberLabelEditParam addParam);

    /**
     * 会员标签编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MemberLabelEditParam editParam);

    /**
     * 会员标签删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 全部会员标签
     * @return
     */
    List<MemberLabelAllListVo> all();
}
