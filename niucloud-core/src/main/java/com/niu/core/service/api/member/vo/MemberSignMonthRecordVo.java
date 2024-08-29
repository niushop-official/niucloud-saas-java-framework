package com.niu.core.service.api.member.vo;

import com.niu.core.entity.member.MemberSign;
import lombok.Data;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.util.List;
import java.util.Map;

@Data
public class MemberSignMonthRecordVo {

    private List<Integer> days;

    private List<Period> period;

    @Data
    public static class Period {
        private String day;

        private Boolean award;
    }
}
