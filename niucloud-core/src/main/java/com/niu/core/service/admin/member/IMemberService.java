package com.niu.core.service.admin.member;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.member.Member;
import com.niu.core.service.admin.member.param.*;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.member.vo.MemberAllListVo;
import com.niu.core.service.admin.member.vo.MemberInfoVo;
import com.niu.core.service.admin.member.vo.MemberListVo;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员服务接口
 */
public interface IMemberService {


    /**
     * 会员列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberListVo>
     */
    PageResult<MemberListVo> list(PageParam pageParam, MemberSearchParam searchParam);

    /**
     * 会员详情
     * @param id 主键ID
     * @return MemberInfoVo
     */
    MemberInfoVo info(Integer id);

    /**
     * 会员添加
     * @param addParam 添加参数
     */
    void add(MemberAddParam addParam);

    /**
     * 会员编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MemberEditParam editParam);

    /**
     * 修改部分字段
     * @param editParam
     */
    void modify(MemberModifyParam editParam);

    /**
     * 会员删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 全部会员
     * @param searchParam
     */
    List<MemberAllListVo> all(MemberSearchParam searchParam);

    /**
     * 设置会员状态
     * @param status
     * @param param
     */
    void setStatus(Integer status, MemberParam param);

    /**
     * 获取会员编码
     * @return
     */
    String getMemberNo();

    /**
     * 获取会员礼包内容
     * @param param
     * @return
     */
    JSONObject getMemberGiftsContent(JSONObject param);

    /**
     * 获取权益内容
     * @param param
     * @return
     */
    JSONObject getMemberBenefitsContent(JSONObject param);

    /**
     * 批量修改
     * @param param
     */
    void batchModify(BatchModifyParam param);
}
