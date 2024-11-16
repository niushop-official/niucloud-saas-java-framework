package com.niu.core.service.api.sys.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.verify.Verify;
import com.niu.core.enums.sys.VerifyTypeEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import lombok.Data;

@Data
public class SysVerifyDetailVo extends Verify {

    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 核销码 */
    private String code;

    /** 核销参数 */
    private String data;

    /** 核销类型 */
    private String type;

    private String typeName;

    /** 核销时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 核销会员id */
    private Integer verifierMemberId;

    /** 核销内容 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String value;

    /** 描述 */
    private String body;

    /** 业务标识 */
    private String relateTag;

    private MemberBriefInfoVo member;

    /**
     * @return
     */
    public String getTypeName() {
        return VerifyTypeEnum.getTypeByName(this.getType());
    }

}
