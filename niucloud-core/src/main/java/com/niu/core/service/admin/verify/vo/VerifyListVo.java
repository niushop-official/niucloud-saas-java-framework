package com.niu.core.service.admin.verify.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.sys.VerifyTypeEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import lombok.Data;

import java.io.Serializable;

/**
 * Verify列表视图
 */
@Data
public class VerifyListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private String code;  // 核销码
    private String data;  // 核销参数
    private String type;  // 核销类型
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 核销时间
    private Integer verifierMemberId;  // 核销会员id
    private String value;  // 核销内容
    private String body;  // 描述
    private String relateTag;  // 业务标识
    private MemberBriefInfoVo member; // 会员信息
    private String typeName;
    private Integer isAdmin;

    public String getTypeName() {
        return VerifyTypeEnum.getTypeByName(this.type);
    }
}
