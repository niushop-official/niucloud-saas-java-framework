package com.niu.core.service.core.member.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class MemberStatSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;
    private String[] createTime;
    private Integer sex;
    private String[] lastVisitTime;
}
