package com.niu.core.common.utils.file;

import com.niu.core.common.utils.system.RuntimeTools;
import com.niu.core.common.config.GlobalConfig;

import java.util.ArrayList;
import java.util.List;

public final class AddonUpgradeHelper {

    /**
     * @param dataList
     */
    public static void cacheAddonInfo(List<String> dataList) {
        cacheAddonInfo(GlobalConfig.applicationName, dataList);
    }

    /**
     * @return
     */
    public static List<String> getAddonInfo() {
        List<String> dataList = getAddonInfo(GlobalConfig.applicationName);
        cacheAddonInfo(GlobalConfig.applicationName,new ArrayList<>());
        return dataList;
    }

    /**
     * @param applicationName
     * @param dataList
     */
    public static void cacheAddonInfo(String applicationName, List<String> dataList) {
        String tmpHome = RuntimeTools.getTmpHome();
        String addonCachePath = tmpHome + "/" + applicationName + "/addon_upgrade.cache";
        FileTools.writeList(addonCachePath, dataList);
    }

    /**
     * @param applicationName
     * @return
     */
    public static List<String> getAddonInfo(String applicationName) {
        String tmpHome = RuntimeTools.getTmpHome();
        String addonCachePath = tmpHome + "/" + applicationName + "/addon_upgrade.cache";
        return FileTools.readToList(addonCachePath);
    }

}
