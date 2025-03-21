package com.niu.core.service.admin.diy_form.vo;


import java.io.Serializable;

import com.niu.core.entity.member.Member;
import lombok.Data;
/**
* 万能表单填写记录列表
*/
@Data
public class DiyFormRecordsListVo implements Serializable {

private static final long serialVersionUID = 1L;

        /** 站点id */
        private Integer siteId;

        /** 所属万能表单id */
        private Integer formId;

        /** 填写人会员id */
        private Integer memberId;


        private Member member;

        private String memberNo;

        private String username;

        private String nickname;

        private String headimg;

        private String mobile;

        private Long writeCount;



}
