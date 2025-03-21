package com.niu.core.service.core.diy_form.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
* 万能表单提交页配置详情
*/
@Data
public class DiyFormSubmitConfigInfoVo implements Serializable {

private static final long serialVersionUID = 1L;


    /** 主键id */
    private Integer id;


    /** 站点id */
    private Integer siteId;


    /** 所属万能表单id */
    private Integer formId;


    /** 填表人提交后操作，text：文字信息，voucher：核销凭证 */
    private String submitAfterAction;


    /** 提示内容类型，default：默认提示，diy：自定义提示 */
    private String tipsType;


    /** 自定义提示内容 */
    private String tipsText;


    /** 核销凭证有效期限制类型，no_limit：不限制，specify_time：指定固定开始结束时间，submission_time：按提交时间设置有效期 */
    private String timeLimitType;


    /** 核销凭证时间限制规则，json格式 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String timeLimitRule;


    /** 核销凭证内容，json格式 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String voucherContentRule;


    /** 填写成功后续操作 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String successAfterAction;


    /** 创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;


    /** 更新时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

}