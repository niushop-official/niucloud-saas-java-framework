package com.niu.core.service.admin.addon.vo;

import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import lombok.Data;

import java.io.Serializable;

/**
 * AddonLogVo
 */
@Data
public class AddonDevelopListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String cover;

    private String desc;

    private String icon;

    private String key;

    private String version;

    private String author;

    private String type;

    private String typeName;

    private String supportApp;

    private Boolean isDownload = true;

    private InstallAddonListVo installInfo;

    public String getTypeName() {
        return AddonTypeEnum.getNameByType(this.type);
    }
}
