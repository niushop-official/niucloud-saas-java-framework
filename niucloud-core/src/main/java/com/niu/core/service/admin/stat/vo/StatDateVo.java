package com.niu.core.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统计数据
 */
@Data
public class StatDateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> date;

    private List<Integer> value;
}
