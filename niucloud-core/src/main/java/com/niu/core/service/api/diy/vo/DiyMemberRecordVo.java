package com.niu.core.service.api.diy.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.core.entity.diy_form.DiyFormRecordsFields;
import lombok.Data;

import java.util.List;

@Data
public class DiyMemberRecordVo {
    private String typeName;
    private Integer formId;
    private String type;
    @JsonProperty("formField")
    private List<DiyFormRecordsFields> formField;
}
