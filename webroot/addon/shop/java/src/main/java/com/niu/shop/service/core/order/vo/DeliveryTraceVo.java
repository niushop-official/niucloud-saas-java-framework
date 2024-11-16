package com.niu.shop.service.core.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
public class DeliveryTraceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success = false;

    private String reason = "";

    private List<Trace> list = new LinkedList<>();

    @Data
    public static class Trace {
        private String datetime;

        private String remark;
    }
}
