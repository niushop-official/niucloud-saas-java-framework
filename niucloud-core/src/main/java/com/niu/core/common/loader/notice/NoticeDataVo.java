package com.niu.core.common.loader.notice;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class NoticeDataVo  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mobile = "";

    private String openid = "";

    private Map<String, Object> vars;

    private Integer memberId = 0;

    private Integer uid = 0;
}
