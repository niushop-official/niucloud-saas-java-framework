package com.niu.core.service.admin.weapp.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信粉丝列参数
*/
@Data
public class SetDomainParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestdomain = "";
    private String wsrequestdomain = "";
    private String uploaddomain = "";
    private String downloaddomain = "";
    private String tcpdomain = "";
    private String udpdomain = "";

}
