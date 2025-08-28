package com.niu.core.service.admin.notice.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterAccountParam implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户名
     */
    private String username;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 验证码
     */
    private String code;

    /**
     * 密码
     */
    private String password;
    private String key;

    /**
     * 签名
     */
    private String signature;

    /**
     * 备注
     */
    private String remark;


    /**
     * 短信实例内容
     */
    @JsonProperty("contentExample")
    private String contentExample;

    /**
     * 企业名称
     */
    @JsonProperty("companyName")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @JsonProperty("creditCode")
    private String creditCode;

    /**
     * 法人姓名
     */
    @JsonProperty("legalPerson")
    private String legalPerson;

    /**
     * 经办人姓名
     */
    @JsonProperty("principalName")
    private String principalName;

    /**
     * 经办人手机号
     */
    @JsonProperty("principalMobile")
    private String principalMobile;

    /**
     * 经办人身份证号
     */
    @JsonProperty("principalIdCard")
    private String principalIdCard;

    /**
     * 签名来源
     */
    @JsonProperty("signSource")
    private Integer signSource;

    /**
     * 签名类型
     */
    @JsonProperty("signType")
    private Integer signType;

    /**
     * 图片
     */
    @JsonProperty("imgUrl")
    private String imgUrl;

    private String defaultVal;

    private Integer defaultSign = 0;
}
