package com.niu.core.service.core.pay.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayTypeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    private String icon;

    private String name;
}
