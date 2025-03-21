package com.niu.core.service.admin.diy_form.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 万能表单表参数
*/
@Data
public class DiyFormDeleteParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 表单id */
    private List<Integer> formIds;


}
