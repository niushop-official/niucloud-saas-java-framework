package com.niu.core.service.admin.notice.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SmsPackageParam {
    private String packageName;
    private String smsNum;
    private String priceStart;
    private String priceEnd;
    private String originalPriceStart;
    private String originalPriceEnd;
    private String timeStart;
    private String timeEnd;
    private Integer page = 1;
    private Integer limit = 15;
}
