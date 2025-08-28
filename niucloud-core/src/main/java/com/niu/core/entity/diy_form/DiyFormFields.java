package com.niu.core.entity.diy_form;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;

/**
* 万能表单字段实体
*/
@Data
public class DiyFormFields implements Serializable {

private static final long serialVersionUID = 1L;

    /** 字段id */
    @TableId(value="field_id", type= IdType.AUTO)
    private Integer fieldId;

    /** 站点id */
    private Integer siteId;

    /** 所属万能表单id */
    private Integer formId;

    /** 字段唯一标识 */
    private String fieldKey;

    /** 字段类型 */
    private String fieldType;

    /** 字段名称 */
    private String fieldName;

    /** 字段说明 */
    private String fieldRemark;

    /** 字段默认值 */
    private String fieldDefault;

    /** 字段填写总数量 */
    private Integer writeNum;

    /** 字段是否必填 0:否 1:是 */
    private Integer fieldRequired;

    /** 字段是否隐藏 0:否 1:是 */
    private Integer fieldHidden;

    /** 字段内容防重复 0:否 1:是 */
    private Integer fieldUnique;

    /** 隐私保护 0:关闭 1:开启 */
    private Integer privacyProtection;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;

    @TableField(exist = false)
    private String type;

}