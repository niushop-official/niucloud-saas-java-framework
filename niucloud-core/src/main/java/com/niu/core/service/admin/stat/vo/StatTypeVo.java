package com.niu.core.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 类型统计类
 */
@Data
public class StatTypeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> type;
    private List<Integer> value;
}
