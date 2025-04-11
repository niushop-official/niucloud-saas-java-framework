package com.niu.core.service.core.pay.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WechatTransferSceneListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private List<String> userRecvPerception;

    private List<String> transferSceneReportInfos;

    private String sceneId;

    private JSONObject tradeSceneData = new JSONObject();
}
