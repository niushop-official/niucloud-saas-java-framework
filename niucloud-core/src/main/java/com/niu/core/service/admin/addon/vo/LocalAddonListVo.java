package com.niu.core.service.admin.addon.vo;

import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class LocalAddonListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String error = "";

    private Map<String, LocalAddonInfoVo> list;

}
