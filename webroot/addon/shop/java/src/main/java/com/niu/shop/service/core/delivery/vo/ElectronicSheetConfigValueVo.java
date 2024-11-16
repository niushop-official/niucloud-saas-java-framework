package com.niu.shop.service.core.delivery.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ElectronicSheetConfigValueVo {
    private static final ElectronicSheetConfigValueVo DEFAULT_CONFIG_VALUE = new ElectronicSheetConfigValueVo();

    @JsonProperty("interface_type")
    private String interfaceType = "kdbird";
    @JsonProperty("kdniao_id")
    private String kdniaoId = "";
    @JsonProperty("kdniao_api_key")
    private String kdniaoApiKey = "";
    @JsonProperty("server_port1")
    private String serverPort1 = "8000";
    @JsonProperty("server_port2")
    private String serverPort2 = "18000";
    @JsonProperty("https_port")
    private String httpsPort = "8443";

    public static ElectronicSheetConfigValueVo newInstance() {
        return DEFAULT_CONFIG_VALUE;
    }
}
