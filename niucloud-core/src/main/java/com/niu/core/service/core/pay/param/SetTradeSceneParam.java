package com.niu.core.service.core.pay.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class SetTradeSceneParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String perception;

    private String scene;

    private String type;

    private JSONObject infos;
}
