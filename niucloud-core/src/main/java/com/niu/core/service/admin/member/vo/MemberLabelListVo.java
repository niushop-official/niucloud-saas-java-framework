package com.niu.core.service.admin.member.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * MemberLabel列表视图
 */
@Data
public class MemberLabelListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer labelId;  // 标签id
    private Integer siteId;  // 站点id
    private String labelName;  // 标签名称
    //会员数量
    private Integer memberNum;
    private String memo;  // 备注
    private Integer sort;  // 排序
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 添加时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime; // 更新时间

}
