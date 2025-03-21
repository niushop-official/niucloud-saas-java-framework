package com.niu.core.entity.diy_form;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;

/**
* 万能表单填写记录实体
*/
@Data
public class DiyFormRecords implements Serializable {

private static final long serialVersionUID = 1L;

    /** 表单填写记录id */
    @TableId(value="record_id", type= IdType.AUTO)
    private Integer recordId;

    /** 站点id */
    private Integer siteId;

    /** 所属万能表单id */
    private Integer formId;

    /** 填写的表单数据 */
    private String value;

    /** 填写人会员id */
    private Integer memberId;

    /** 关联业务id */
    private Integer relateId;

    /** 创建时间 */
    private Long createTime;

}