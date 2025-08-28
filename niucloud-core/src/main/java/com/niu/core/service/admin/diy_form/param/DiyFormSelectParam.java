package com.niu.core.service.admin.diy_form.param;

import lombok.Data;

import java.util.List;

@Data
public class DiyFormSelectParam {

    private String title;
    private String type;
    private String addon;
    private List<Integer> verifyFormIds;
}
