package com.niu.core.service.admin.weapp.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 微信粉丝列参数
*/
@Data
public class WeappVersionAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String desc = "";

}
