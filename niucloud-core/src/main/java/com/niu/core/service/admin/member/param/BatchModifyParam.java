package com.niu.core.service.admin.member.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BatchModifyParam {

    @NotEmpty(message = "field不能为空")
    private String field;

    private Integer isAll = 0;

    @NotEmpty(message = "memberIds不能为空")
    private List<Integer> memberIds;

    private Object value;

    private Where where;
    @Data
    public static class Where {
        private List<String> createTime;
        private String keyword;
        private String memberLabel;
        private String memberLevel;
        private String registerChannel;
        private String registerType;
    }
}
