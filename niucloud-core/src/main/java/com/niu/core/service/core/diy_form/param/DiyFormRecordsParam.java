package com.niu.core.service.core.diy_form.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serializable;

/**
 * 万能表单填写记录参数
*/
@Data
public class DiyFormRecordsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 表单填写记录id */
    private Integer recordId;

    /** 站点id */
    private Integer siteId;

    /** 所属万能表单id */
    private Integer formId;

    /** 填写的表单数据 */
    private JSONArray value;

    /** 填写人会员id */
    private Integer memberId;

    /** 关联业务id */
    private Integer relateId;


}
