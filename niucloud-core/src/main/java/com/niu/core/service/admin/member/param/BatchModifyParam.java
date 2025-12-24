package com.niu.core.service.admin.member.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BatchModifyParam {

    @NotEmpty(message = "field不能为空")
    private String field;

    private Integer isAll = 0;

    private List<Integer> memberIds;

    private Object value;

    private Where where;
    @Data
    public static class Where {
        /** 关键字 */
        private String keyword;

        /** 注册类型 */
        private String registerType;

        /** 注册途径 */
        private String registerChannel;

        /** 创建时间筛选 */
        private String[] createTime;

        private Integer memberLabel;

        /** 会员等级 */
        private Integer memberLevel;
    }
}
