package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SpreadQrcodeParam {

    private String page;

    private String folder;

    private List<Param> params;

    @Data
    public static class Param {
        private Object columnName;
        private String columnValue;
    }
}
