package com.niu.shop.service.core.delivery.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TimeListVo implements Serializable {

    private String name;

    private List<String> timeList;

}
