package com.niu.core.service.admin.upgrade.param;

import lombok.Data;

@Data
public class UpgradeParam {

    private String addon = "";

    private Boolean isNeedBackup = true;

    private Boolean isNeedCloudbuild = true;

}
