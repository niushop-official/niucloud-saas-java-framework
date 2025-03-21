package com.niu.core.service.admin.member.param;
import cn.hutool.json.JSONObject;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 会员等级参数
*/
@Data
public class MemberLevelParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "level_name参数缺失")
    @NotBlank(message = "等级名称不能为空")
    private String levelName;

    @NotNull(message = "growth参数缺失")
    private Integer growth;

    @NotNull(message = "remark参数缺失")
    private String remark;

    @NotNull(message = "level_benefits参数缺失")
    private JSONObject levelBenefits;

    @NotNull(message = "level_gifts参数缺失")
    private JSONObject levelGifts;

}
