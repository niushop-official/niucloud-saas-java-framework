package com.niu.core.enums.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 协议管理枚举
 */
public class AgreementEnum {

    /**
     * 协议类型
     *
     * @return
     */
    public static JSONObject getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("sys/agreement.json");
    }

    /**
     * 获取协议类型名称
     *
     * @param key
     * @return
     */
    public static String getTypeName(String key){
          return getType().getStr(key);
    }
}
