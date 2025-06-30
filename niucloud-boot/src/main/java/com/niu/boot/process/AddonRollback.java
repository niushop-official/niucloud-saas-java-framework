package com.niu.boot.process;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.boot.utils.BootLogUtils;
import com.niu.boot.utils.FileTools;
import com.niu.boot.utils.JsonLoadUtils;

import java.io.File;

public class AddonRollback {

    /**
     * @param command
     * @param jarDir
     * @return
     */
    public static boolean execute(String command, String jarDir) {
        String addonName = "";
        if (command.contains("uninstalled,")) {
            addonName = command.replaceAll("uninstalled,", "");
            BootLogUtils.log("正在卸载：" + addonName);
        } else if (command.contains("rollback,")) {
            addonName = command.replaceAll("rollback,", "");
            BootLogUtils.log("正在回滚：" + addonName);
        }
        for (int i = 0; i < 20; i++) {
            BootLogUtils.log(".");
        }
        String addonRunDescPath = jarDir + "/addon/addon.json";
        JSONArray runAddonJsonArray = JsonLoadUtils.readFileToJSONArray(addonRunDescPath);
        if (runAddonJsonArray.size() > 0) {
            runAddonJsonArray = removeContainedAddon(runAddonJsonArray, addonName);
        }
        String formattedJsonStr = JSONUtil.formatJsonStr(JSONUtil.toJsonStr(runAddonJsonArray));
        FileTools.write(addonRunDescPath, formattedJsonStr);
        // 如果jar文件存在，删除jar文件
        String addonJarPath = jarDir + "/addon/" + addonName + ".jar";
        File addonJarFile = new File(addonJarPath);
        if (addonJarFile.exists()) {
            boolean isDeleted = addonJarFile.delete();
            if (isDeleted) {
                BootLogUtils.log("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
                BootLogUtils.log("插件文件：" + addonJarPath + ",已被删除...........");
                BootLogUtils.log("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
            } else {
                BootLogUtils.log("×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××");
                BootLogUtils.log("插件文件：" + addonJarPath + ",无法删除...........");
                BootLogUtils.log("×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××");
            }
        }
        if (command.contains("uninstalled,")) {
            BootLogUtils.log("卸载完成：" + addonName);
        } else if (command.contains("rollback,")) {
            BootLogUtils.log("回滚完成：" + addonName);
        }
        return true;
    }

    /**
     * @param runAddonJsonArray
     * @param addonName
     * @return
     */
    private static JSONArray removeContainedAddon(JSONArray runAddonJsonArray, String addonName) {
        if (runAddonJsonArray == null || runAddonJsonArray.size() <= 0) {
            return runAddonJsonArray;
        }
        int index = -1;
        for (int i = 0; i < runAddonJsonArray.size(); i++) {
            JSONObject jsonObject = runAddonJsonArray.getJSONObject(i);
            String name = jsonObject.getStr("name");
            if (name.equals(addonName)) {
                index = i;
            }
        }
        if (index != -1) runAddonJsonArray.remove(index);
        return runAddonJsonArray;
    }

}
