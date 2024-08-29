package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 协议参数
 */
@Data
public class SysAgreementSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String agreementKey;

    private String title;

    private String content;

}
