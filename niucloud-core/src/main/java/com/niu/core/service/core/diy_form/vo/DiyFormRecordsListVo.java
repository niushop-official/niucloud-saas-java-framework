package com.niu.core.service.core.diy_form.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.member.Member;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
* 万能表单填写记录列表
*/
@Data
public class DiyFormRecordsListVo implements Serializable {

private static final long serialVersionUID = 1L;

        /** 表单填写记录id */
        private Integer recordId;

        /** 站点id */
        private Integer siteId;

        /** 所属万能表单id */
        private Integer formId;

        /** 填写的表单数据 */
        private String value;

        /** 填写人会员id */
        private Integer memberId;


        private Member member;

        private String memberNo;

        private String username;

        private String nickname;

        private String headimg;

        private String mobile;


        /** 关联业务id */
        private Integer relateId;

        /** 创建时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;

        @JsonProperty("recordsFieldList")
        private Map< String, DiyFormRecordsFieldsListVo> recordsFieldList;

        private String pageTitle;

        private String title;

        private String type;



}
