package com.niu.core.service.core.channel.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 支付宝小程序配置视图
 */

@Data
public class H5ConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isOpen = 1;

}