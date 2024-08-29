package com.niu.core.component.context;

import com.niu.core.common.utils.system.RuntimeTools;
import com.niu.core.config.GlobalConfig;

import java.io.File;

public final class WebAppEnvs {

    public String envType;

    public String projectRoot;

    public String projectNiucloudAddon;

    public String webRoot;

    public String webRootDownAddon;

    public String webRootDownJar;

    public String webRootDownPublic;

    public String webRootDownResource;

    public String webRootDownRuntime;

    @Override
    public String toString() {
        return "WebAppEnvs " +
                "\n{" +
                "\n    envType='" + envType + '\'' +
                ",\n    projectRoot='" + projectRoot + '\'' +
                ", \n    projectNiucloudAddon='" + projectNiucloudAddon + '\'' +
                ", \n    webRoot='" + webRoot + '\'' +
                ", \n    webRootDownAddon='" + webRootDownAddon + '\'' +
                ", \n    webRootDownJar='" + webRootDownJar + '\'' +
                ", \n    webRootDownPublic='" + webRootDownPublic + '\'' +
                ", \n    webRootDownResource='" + webRootDownResource + '\'' +
                ", \n    webRootDownRuntime='" + webRootDownRuntime + '\'' +
                "\n}";
    }

    /**
     * @return
     */
    public static WebAppEnvs get() {
        return get(GlobalConfig.runActive);
    }

    /**
     * @param active
     * @return
     */
    public static WebAppEnvs get(String active) {
        WebAppEnvs appEnvs = null;
        if ("dev".equals(active)) {
            appEnvs = new WebAppEnvs();
            appEnvs.envType = "dev";
            appEnvs.projectRoot = System.getProperty("user.dir");
            appEnvs.projectNiucloudAddon = appEnvs.projectRoot + "/niucloud-addon/";
            appEnvs.webRoot = appEnvs.projectRoot + "/webroot/";
            appEnvs.webRootDownAddon = appEnvs.projectRoot + "/webroot/addon/";
            appEnvs.webRootDownJar = appEnvs.projectRoot + "/webroot/jar/";
            appEnvs.webRootDownPublic = appEnvs.projectRoot + "/webroot/public/";
            appEnvs.webRootDownResource = appEnvs.projectRoot + "/webroot/resource/";
            appEnvs.webRootDownRuntime = appEnvs.projectRoot + "/webroot/runtime/";
        } else {
            appEnvs = new WebAppEnvs();
            appEnvs.envType = "prod";
            appEnvs.webRoot = RuntimeTools.getWebRootPath();
            File webRootFile = new File(appEnvs.webRoot);
            appEnvs.projectRoot = webRootFile.getParent();
            appEnvs.projectNiucloudAddon = appEnvs.projectRoot + "/niucloud-addon/";
            appEnvs.webRootDownAddon = appEnvs.projectRoot + "/webroot/addon/";
            appEnvs.webRootDownJar = appEnvs.projectRoot + "/webroot/jar/";
            appEnvs.webRootDownPublic = appEnvs.projectRoot + "/webroot/public/";
            appEnvs.webRootDownResource = appEnvs.projectRoot + "/webroot/resource/";
            appEnvs.webRootDownRuntime = appEnvs.projectRoot + "/webroot/runtime/";
        }
        return appEnvs;
    }

    /**
     * @return
     */
    private static String getCurrentRunPath() {
        return WebAppEnvs.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

}
