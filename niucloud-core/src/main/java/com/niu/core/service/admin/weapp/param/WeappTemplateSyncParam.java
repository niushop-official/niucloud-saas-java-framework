package com.niu.core.service.admin.weapp.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 微信粉丝列参数
*/
@Data
public class WeappTemplateSyncParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "key参数缺失")
    private String[] keys;


}
