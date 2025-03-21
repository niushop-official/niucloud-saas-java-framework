package com.niu.core.service.admin.site.param;
import cn.hutool.json.JSONArray;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 店铺分组（分组权限）参数
*/
@Data
public class SiteGroupParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "groupId参数缺失")
    private Integer groupId;

    @NotNull(message = "groupName参数缺失")
    private String groupName;

    @NotNull(message = "groupDesc参数缺失")
    private String groupDesc;

    @NotNull(message = "app参数缺失")
    private JSONArray app;

    @NotNull(message = "addon参数缺失")
    private JSONArray addon;

}
