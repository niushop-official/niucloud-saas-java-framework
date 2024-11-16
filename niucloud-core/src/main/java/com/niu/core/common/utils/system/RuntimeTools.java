package com.niu.core.common.utils.system;

import com.niu.core.common.component.context.WebAppEnvs;

import java.io.File;
import java.net.URLDecoder;

public class RuntimeTools {

    /**
     * 操作系统名称
     *
     * @return
     */
    public static String osName() {
        String osName = System.getProperty("os.name");
        return osName;
    }

    /**
     * 返回临时目录
     *
     * @return
     */
    public static String getTmpHome() {
        if (osName().contains("Windows")) {
            return System.getProperty("java.io.tmpdir");
        } else {
            File file = new File(WebAppEnvs.get().webRootDownJar + "/Temp/");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
    }

    /**
     * 获取运行时Home的路径
     *
     * @param clazz
     * @return
     */
    public static String getRunPath(Class clazz) {
        String encoding = System.getProperty("sun.jnu.encoding");
        if (encoding == null || "".equals(encoding)) {
            encoding = "UTF-8";
        }
        String runPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodeRunPath = null;
        try {
            decodeRunPath = URLDecoder.decode(runPath, encoding);
        } catch (Exception e) {
        }
        String lowerCaseRunPath = decodeRunPath.toLowerCase();
        if (lowerCaseRunPath.contains(".jar")) {
            int jarIndex = lowerCaseRunPath.indexOf(".jar");
            decodeRunPath = decodeRunPath.substring(0, jarIndex);
        }
        return new File(decodeRunPath).getParent();
    }

    /**
     * 生产环境获取"webroot"的根路径
     *
     * @return
     */
    public static String getWebRootPath() {
        String runPath = RuntimeTools.getRunPath(RuntimeTools.class);
        if (runPath.startsWith("file:\\")) {
            runPath = runPath.substring(6, runPath.length());
        }
        if (runPath.startsWith("file:")) {
            runPath = runPath.substring(5, runPath.length());
        }
        String webroot = runPath;
        if (webroot.contains("webroot")) {
            webroot = webroot.substring(0, webroot.indexOf("webroot") + 7);
        }
        return webroot;
    }

}
