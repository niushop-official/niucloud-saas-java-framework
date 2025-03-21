package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class PreviewPosterParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String type;

    private String channel = "h5";

}
