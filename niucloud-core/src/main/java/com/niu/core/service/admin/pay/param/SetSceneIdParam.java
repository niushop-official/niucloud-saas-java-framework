package com.niu.core.service.admin.pay.param;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
public class SetSceneIdParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String scene;

    private String sceneId;
}
