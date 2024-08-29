package com.niu.core.service.admin.sys.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.sys.AgreementEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * SysAgreement列表视图
 */
@Data
public class SysAgreementListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 协议关键字 */
    private String agreementKey;

    private String typeName;
    /** 协议标题 */
    private String title;

    /** 协议内容 */
    private String content;

    /** 添加时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 修改时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    public String getTypeName() {
        return AgreementEnum.getTypeName(this.agreementKey);
    }
}
