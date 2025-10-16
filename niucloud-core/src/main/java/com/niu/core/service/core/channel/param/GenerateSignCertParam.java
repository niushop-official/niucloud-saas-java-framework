package com.niu.core.service.core.channel.param;

import lombok.Data;

@Data
public class GenerateSignCertParam {

    private String keyAlias = "";
    private String keyPassword = "";
    private String storePassword = "";
    private Integer limit = 30;
    private String cn = "";
    private String o = "";
    private String ou = "";
    private String c = "";
    private String st = "";
    private String l = "";

}
