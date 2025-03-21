package com.niu.core.service.admin.diy_form.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import java.io.Serializable;
import lombok.Data;

/**
* 万能表单填写字段表详情
*/
@Data
public class DiyFormRecordsFieldsInfoVo implements Serializable {

private static final long serialVersionUID = 1L;


    /**  */
    private Integer id;


    /** 站点id */
    private Integer siteId;


    /** 所属万能表单id */
    private Integer formId;


    /** 关联表单字段id */
    private Integer formFieldId;


    /** 关联表单填写记录id */
    private Integer recordId;


    /** 填写会员id */
    private Integer memberId;


    /** 字段唯一标识 */
    private String fieldKey;


    /** 字段类型 */
    private String fieldType;


    /** 字段名称 */
    private String fieldName;


    /** 字段值，根据类型展示对应效果 */
    private String fieldValue;


    /** 字段是否必填 0:否 1:是 */
    private Integer fieldRequired;


    /** 字段是否隐藏 0:否 1:是 */
    private Integer fieldHidden;


    /** 字段内容防重复 0:否 1:是 */
    private Integer fieldUnique;


    /** 隐私保护 0:关闭 1:开启 */
    private Integer privacyProtection;


    /** 字段修改次数 */
    private Integer updateNum;


    /** 创建时间 */
    private Long createTime;


    /** 修改时间 */
    private Long updateTime;

}