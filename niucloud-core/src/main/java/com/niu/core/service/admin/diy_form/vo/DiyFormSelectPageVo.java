package com.niu.core.service.admin.diy_form.vo;

import com.niu.core.common.domain.PageResult;
import lombok.Data;

import java.util.List;

@Data
public class DiyFormSelectPageVo extends PageResult<DiyFormInfoVo> {

    private List<Integer> verifyFormIds;
}
