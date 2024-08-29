package com.niu.core.service.admin.home.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 添加站点参数
 *
*/
@Data
public class HomeSiteAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**套餐id */
    @NotNull(message = "group_id参数缺失")
    private Integer groupId;

    /**站点名称 */
    @NotNull(message = "siteName参数缺失")
    private String siteName;

}
