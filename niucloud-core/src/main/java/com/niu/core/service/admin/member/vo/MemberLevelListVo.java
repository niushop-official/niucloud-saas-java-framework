package com.niu.core.service.admin.member.vo;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
import lombok.Data;

import jakarta.annotation.Resource;
import java.io.Serializable;

/**
 * MemberLevel列表视图
 */
@Data
public class MemberLevelListVo implements Serializable {

    @Resource
    MemberMapper memberMapper;

    private static final long serialVersionUID = 1L;

    private Integer levelId;  // 会员等级
    private Integer siteId;  // 站点id
    private String levelName;  // 等级名称
    private Integer growth;  // 所需成长值
    private String remark;  // 备注
    private Integer status;  // 状态 0已禁用1已启用
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 添加时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime; // 更新时间
    private JSONObject levelBenefits;  // 等级权益
    private JSONObject levelGifts;  // 等级礼包
    private Long memberNum;
}
