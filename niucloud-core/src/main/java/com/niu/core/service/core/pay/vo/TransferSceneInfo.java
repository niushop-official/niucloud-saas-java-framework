package com.niu.core.service.core.pay.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class TransferSceneInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String infos;

    /** 转账收款感知 */
    private String perception;

    /** 场景 */
    private String scene;

    private String sceneId;

    /** 业务类型 */
    private String type;
}
