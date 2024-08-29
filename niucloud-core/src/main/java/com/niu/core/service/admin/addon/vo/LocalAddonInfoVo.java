package com.niu.core.service.admin.addon.vo;

import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class LocalAddonInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String desc;

    private String key;

    private String version;

    private String author;

    private String type;

    private String supportApp;

    private Boolean isDownload = false;

    private Boolean isLocal = false;

    private String icon;

    private String cover;

    private InstallAddonListVo installInfo;
}
