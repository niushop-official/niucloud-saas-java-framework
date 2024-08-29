package com.niu.core.service.admin.site.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 店铺分组（分组权限）参数
 */
@Data
public class SiteGroupSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String keywords;

}
