package com.niu.core.service.api.login.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SendMobileCodeVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String key;

}
